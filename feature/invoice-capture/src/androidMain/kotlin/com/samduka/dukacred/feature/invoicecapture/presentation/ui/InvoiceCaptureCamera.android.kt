package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text as MlKitText
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.samduka.dukacred.feature.invoicecapture.util.BRIGHTNESS_THRESHOLD
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.preat.peekaboo.ui.camera.CameraMode
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.PeekabooCameraState
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

actual class InvoiceCaptureCameraController(
    private val context: Context,
    private val onCapture: (ByteArray?) -> Unit,
) {
    // ── Threading ──────────────────────────────────────────────────────────────
    // Single-thread pool for ImageAnalysis — CameraX requires a dedicated executor
    private val analysisExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    // All Compose state mutations happen here, keeping mutableStateOf thread-safe
    private val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
    private val mainHandler = Handler(Looper.getMainLooper())

    // ── CameraX handles ────────────────────────────────────────────────────────
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var boundOwner: LifecycleOwner? = null  // idempotent binding guard

    // ── ML Kit ─────────────────────────────────────────────────────────────────
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // ── Analysis throttle ──────────────────────────────────────────────────────
    // 2 fps is plenty for document detection and saves battery / reduces latency
    @Volatile private var lastAnalysisMs = 0L
    private val analysisIntervalMs = 500L

    // ── Thread-safe capture flag ───────────────────────────────────────────────
    // AtomicBoolean is read from the analyser thread; mutableStateOf is for Compose
    private val capturingAtomic = AtomicBoolean(false)

    // ── Compose-observable state (only written on the main thread) ─────────────
    private var _isCameraReady   by mutableStateOf(false)
    private var _isCapturing     by mutableStateOf(false)
    private var _hasInvoiceText  by mutableStateOf(false)
    private var _hasAdequateLight by mutableStateOf(true)   // optimistic until first frame

    actual val isCameraReady: Boolean    get() = _isCameraReady
    actual val isCapturing: Boolean      get() = _isCapturing
    actual val hasInvoiceText: Boolean   get() = _hasInvoiceText
    actual val hasAdequateLight: Boolean get() = _hasAdequateLight

    // ── Camera binding ─────────────────────────────────────────────────────────
    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        // Called on every recomposition from AndroidView.update — guard is critical
        if (boundOwner === lifecycleOwner && _isCameraReady) return
        boundOwner = lifecycleOwner
        _isCameraReady = false

        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            cameraProvider = future.get()

            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                // YUV_420_888 gives us direct Y-plane access for brightness
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .also { it.setAnalyzer(analysisExecutor, ::analyzeFrame) }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview, imageCapture, analysis,
                )
                _isCameraReady = true
            } catch (e: Exception) {
                _isCameraReady = false
            }
        }, mainExecutor)
    }

    // ── Frame analysis (runs on analysisExecutor — background thread) ──────────
    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    private fun analyzeFrame(proxy: ImageProxy) {
        // Skip analysis entirely while a still capture is pending
        if (capturingAtomic.get()) { proxy.close(); return }

        // ── Brightness check (cheap — always runs, even on throttled frames) ──
        val brightness = yPlaneBrightness(proxy)
        mainHandler.post { _hasAdequateLight = brightness >= BRIGHTNESS_THRESHOLD }

        // ── Throttle ML Kit to 2 fps ──────────────────────────────────────────
        val now = System.currentTimeMillis()
        if (now - lastAnalysisMs < analysisIntervalMs) {
            proxy.close()
            return
        }
        lastAnalysisMs = now

        // Skip ML Kit in the dark — saves GPU cycles and gives clear user signal
        val mediaImage = proxy.image
        if (mediaImage == null || brightness < BRIGHTNESS_THRESHOLD) {
            mainHandler.post { _hasInvoiceText = false }
            proxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
        textRecognizer.process(inputImage)
            .addOnSuccessListener(mainExecutor) { text ->
                _hasInvoiceText = isInvoiceStructurePresent(text)
            }
            .addOnFailureListener(mainExecutor) {
                _hasInvoiceText = false
            }
            // Always close on complete — unblocks the next frame from CameraX
            .addOnCompleteListener(mainExecutor) { proxy.close() }
    }

    // ── Brightness via Y-plane sampling (no byte array copy) ──────────────────
    private fun yPlaneBrightness(proxy: ImageProxy): Float {
        val plane  = proxy.planes[0]
        val buf    = plane.buffer
        val stride = plane.rowStride
        val step   = 16         // sample 1 pixel in every 16×16 block
        var sum    = 0L
        var count  = 0
        var row    = 0
        while (row < proxy.height) {
            var col = 0
            while (col < proxy.width) {
                val idx = row * stride + col
                if (idx < buf.limit()) { sum += buf.get(idx).toInt() and 0xFF; count++ }
                col += step
            }
            row += step
        }
        return if (count == 0) 1f else (sum.toFloat() / count) / 255f
    }

    // ── Invoice structure heuristic ────────────────────────────────────────────
    private fun isInvoiceStructurePresent(text: MlKitText): Boolean {
        val blocks = text.textBlocks
        if (blocks.size < 2) return false                         // bare minimum

        val fullText = text.text.lowercase()
        val keywords = setOf(
            "invoice", "receipt", "total", "amount", "date", "due",
            "subtotal", "tax", "vat", "qty", "quantity", "bill",
            "kra", "pin", "eti", "ksh", "kes", "mpesa",          // KE fintech terms
        )
        val keywordHits = keywords.count { fullText.contains(it) }
        // At least one currency/numeric pattern (handles "1,250.00", "500", "1.5K")
        val hasFigures = blocks.any { b ->
            b.text.contains(Regex("""[\d,]+\.?\d{0,2}"""))
        }
        return keywordHits >= 2 && hasFigures
    }

    // ── Still capture ──────────────────────────────────────────────────────────
    actual fun capture() {
        val cap = imageCapture ?: return
        // compareAndSet prevents double-fires from rapid effect collection
        if (!capturingAtomic.compareAndSet(false, true)) return
        _isCapturing = true

        cap.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // planes[0] of an ImageCapture callback is always JPEG bytes
                val buf   = image.planes[0].buffer
                val bytes = ByteArray(buf.remaining()).also { buf.get(it) }
                image.close()
                capturingAtomic.set(false)
                _isCapturing = false
                onCapture(bytes)
            }
            override fun onError(e: ImageCaptureException) {
                capturingAtomic.set(false)
                _isCapturing = false
                onCapture(null)
            }
        })
    }

    // ── Cleanup ────────────────────────────────────────────────────────────────
    fun release() {
        cameraProvider?.unbindAll()
        cameraProvider   = null
        boundOwner       = null
        _isCameraReady   = false
        textRecognizer.close()
        analysisExecutor.shutdown()
    }
}

@Composable
actual fun rememberInvoiceCaptureCameraController(
    onCapture: (ByteArray?) -> Unit
): InvoiceCaptureCameraController {
    val context = LocalContext.current
    val state = rememberPeekabooCameraState(
        initialCameraMode = CameraMode.Back,
        onCapture = onCapture
    )
    return remember(state) { InvoiceCaptureCameraController(context,onCapture) }
}

@Composable
actual fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    // We need the lifecycle owner to bind the native CameraX feed
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. Check if we ALREADY have permission
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 2. Register the native Android permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // 3. Request permission on first composition if we don't have it
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // 4. Render conditionally based on the OS response
    if (hasPermission) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    // Required for ImageAnalysis to run concurrently with Preview
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            update = { previewView ->
                // Wire the native view to our controller logic
                controller.bindCamera(lifecycleOwner, previewView)
            }
        )
    } else {
        permissionDeniedContent()
    }
}
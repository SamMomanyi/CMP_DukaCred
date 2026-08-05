package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text as MlKitText
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.samduka.dukacred.feature.invoicecapture.util.BRIGHTNESS_THRESHOLD
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "ML_KIT_DEBUG"

actual class InvoiceCaptureCameraController(
    private val context: Context,
    private val onCapture: (ByteArray?) -> Unit,
) {
    private val analysisExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
    private val mainHandler = Handler(Looper.getMainLooper())

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var boundOwner: LifecycleOwner? = null

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @Volatile private var lastAnalysisMs = 0L
    private val analysisIntervalMs = 500L

    private val capturingAtomic = AtomicBoolean(false)

    private var _isCameraReady by mutableStateOf(false)
    private var _isCapturing by mutableStateOf(false)
    private var _hasInvoiceText by mutableStateOf(false)
    private var _hasAdequateLight by mutableStateOf(true)

    actual val isCameraReady: Boolean get() = _isCameraReady
    actual val isCapturing: Boolean get() = _isCapturing
    actual val hasInvoiceText: Boolean get() = _hasInvoiceText
    actual val hasAdequateLight: Boolean get() = _hasAdequateLight

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
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
                android.util.Log.d(TAG, "Camera bound successfully — analyzer attached.")
            } catch (e: Exception) {
                android.util.Log.e(TAG, "bindToLifecycle FAILED — analyzer never attaches", e)
                _isCameraReady = false
            }
        }, mainExecutor)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun analyzeFrame(proxy: ImageProxy) {
        if (capturingAtomic.get()) { proxy.close(); return }

        val brightness = yPlaneBrightness(proxy)
        mainHandler.post {
            _hasAdequateLight = brightness >= BRIGHTNESS_THRESHOLD
        }
        android.util.Log.d(TAG, "brightness=$brightness threshold=$BRIGHTNESS_THRESHOLD")

        val now = System.currentTimeMillis()
        if (now - lastAnalysisMs < analysisIntervalMs) {
            proxy.close()
            return
        }
        lastAnalysisMs = now

        val mediaImage = proxy.image
        if (mediaImage == null) {
            mainHandler.post { _hasInvoiceText = false }
            proxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
        textRecognizer.process(inputImage)
            .addOnSuccessListener(mainExecutor) { text ->
                android.util.Log.d(TAG, "--- VISION ENGINE START LOG ---")
                if (text.text.isEmpty()) {
                    android.util.Log.w(TAG, "Found NO text in this frame.")
                } else {
                    android.util.Log.d(TAG, "Raw Interpreted Text:\n${text.text}")
                    text.textBlocks.forEachIndexed { index, block ->
                        android.util.Log.d(TAG, "Block $index: ${block.text.replace("\n", " ")}")
                    }
                }
                val detected = isInvoiceStructurePresent(text)
                android.util.Log.d(TAG, "isInvoiceStructurePresent -> $detected")
                _hasInvoiceText = detected
            }
            .addOnFailureListener(mainExecutor) { e ->
                android.util.Log.e(TAG, "ML Kit failed to process image", e)
                _hasInvoiceText = false
            }
            .addOnCompleteListener(mainExecutor) { proxy.close() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun yPlaneBrightness(proxy: ImageProxy): Float {
        val plane = proxy.planes[0]
        val buf = plane.buffer
        val stride = plane.rowStride
        val step = 16
        var sum = 0L
        var count = 0
        var row = 0
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

    private fun isInvoiceStructurePresent(text: MlKitText): Boolean {
        val blocks = text.textBlocks
        if (blocks.isEmpty()) return false

        val fullText = text.text.lowercase()
        val keywords = setOf(
            "invoice", "receipt", "total", "amount", "date", "due",
            "subtotal", "tax", "vat", "qty", "quantity", "bill",
            "kra", "pin", "eti", "ksh", "kes", "mpesa",
        )
        val decimalCount = Regex("""[\d,]+\.\d{1,2}""").findAll(fullText).count()
        if (decimalCount >= 2) return true

        val currencyCount = Regex(
            """(?:KSh|Ksh|KES|ksh|USD|EUR|\$|€|£)\s*[\d,]+|[\d,]+\s*(?:KSh|Ksh|KES|ksh)""",
            RegexOption.IGNORE_CASE,
        ).findAll(fullText).count()

        val keywordHits = keywords.count { fullText.contains(it) }
        val hasFigures = blocks.any { b -> b.text.contains(Regex("""[\d,]+\.?\d{0,2}""")) }

        return currencyCount >= 2 || (keywordHits >= 2 && hasFigures)
    }

    actual fun capture() {
        val cap = imageCapture ?: return
        if (!capturingAtomic.compareAndSet(false, true)) return
        _isCapturing = true

        cap.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val buf = image.planes[0].buffer
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

    fun release() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        boundOwner = null
        _isCameraReady = false
        textRecognizer.close()
        analysisExecutor.shutdown()
    }
}

@Composable
actual fun rememberInvoiceCaptureCameraController(
    onCapture: (ByteArray?) -> Unit
): InvoiceCaptureCameraController {
    val context = LocalContext.current
    return remember { InvoiceCaptureCameraController(context, onCapture) }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
actual fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasPermission = isGranted }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            update = { previewView ->
                controller.bindCamera(lifecycleOwner, previewView)
            }
        )
    } else {
        permissionDeniedContent()
    }
}
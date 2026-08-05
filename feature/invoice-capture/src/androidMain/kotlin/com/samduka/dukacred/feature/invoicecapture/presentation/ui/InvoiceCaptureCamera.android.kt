// androidMain/.../presentation/ui/InvoiceCaptureCameraController.android.kt
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview as CXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.samduka.dukacred.feature.invoicecapture.util.BRIGHTNESS_THRESHOLD
import com.samduka.dukacred.feature.invoicecapture.util.analyzeBrightness
import java.util.concurrent.Executors

actual class rInvoiceCaptureCameraController(
    private val onCapture: (ByteArray?) -> Unit,
) {
    // ── Reactive state ────────────────────────────────────────────────────────
    // IMPORTANT: these MUST be `mutableStateOf`, not plain vars. snapshotFlow()
    // in the Screen only observes Compose *snapshot state* reads — mutating a
    // plain var from the analyzer's background thread is invisible to it, and
    // the UI will simply never react even though the field is "correct".
    private var isCameraReadyState by mutableStateOf(false)
    private var isCapturingState by mutableStateOf(false)
    private var hasInvoiceTextState by mutableStateOf(false)
    private var hasAdequateLightState by mutableStateOf(false)

    actual val isCameraReady: Boolean get() = isCameraReadyState
    actual val isCapturing: Boolean get() = isCapturingState
    actual val hasInvoiceText: Boolean get() = hasInvoiceTextState
    actual val hasAdequateLight: Boolean get() = hasAdequateLightState

    private val analysisExecutor = Executors.newSingleThreadExecutor()
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var imageCapture: ImageCapture? = null
    internal var previewView: PreviewView? = null

    @SuppressLint("UnsafeOptInUsageError")
    internal fun bindToLifecycle(provider: ProcessCameraProvider, lifecycleOwner: LifecycleOwner) {
        val preview = CXPreview.Builder().build().also {
            it.setSurfaceProvider(previewView?.surfaceProvider)
        }

        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        imageCapture = capture

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { it.setAnalyzer(analysisExecutor, ::processFrame) }

        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            capture,
            analysis,
        )

        isCameraReadyState = true
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processFrame(imageProxy: ImageProxy) {
        // 1. Luminance — cheap, do it before touching ML Kit.
        val yPlane = imageProxy.planes[0].buffer
        val bytes = ByteArray(yPlane.remaining())
        yPlane.get(bytes)
        hasAdequateLightState = analyzeBrightness(bytes) >= BRIGHTNESS_THRESHOLD

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                // Tune this heuristic — length of recognized text is a cheap
                // proxy for "something document-shaped is in frame".
                hasInvoiceTextState = visionText.text.trim().length > 12
            }
            .addOnFailureListener {
                hasInvoiceTextState = false
            }
            .addOnCompleteListener {
                // 🔑 THE FIX: without this, ImageAnalysis delivers exactly ONE
                // frame and then stalls forever — looks identical to "auto
                // detection doesn't react" from the UI's perspective.
                imageProxy.close()
            }
    }

    internal fun takePicture() {
        val capture = imageCapture ?: return onCapture(null)
        isCapturingState = true
        capture.takePicture(
            analysisExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    image.close()
                    isCapturingState = false
                    onCapture(bytes)
                }

                override fun onError(exception: ImageCaptureException) {
                    isCapturingState = false
                    onCapture(null)
                }
            },
        )
    }

    actual fun capture() = takePicture()

    internal fun release() {
        recognizer.close()
        analysisExecutor.shutdown()
    }
}

@Composable
actual fun rememberInvoiceCaptureCameraController(
    onCapture: (ByteArray?) -> Unit,
): InvoiceCaptureCameraController {
    val controller = remember { InvoiceCaptureCameraController(onCapture) }
    DisposableEffect(Unit) { onDispose { controller.release() } }
    return controller
}

@Composable
actual fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit,
) {
    val context: Context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!hasPermission) {
        permissionDeniedContent()
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                controller.previewView = this
                val providerFuture = ProcessCameraProvider.getInstance(ctx)
                providerFuture.addListener(
                    { controller.bindToLifecycle(providerFuture.get(), lifecycleOwner) },
                    ContextCompat.getMainExecutor(ctx),
                )
            }
        },
    )
}
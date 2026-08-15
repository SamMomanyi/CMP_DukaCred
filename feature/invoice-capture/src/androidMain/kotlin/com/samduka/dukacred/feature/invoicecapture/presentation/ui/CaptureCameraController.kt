
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.samduka.dukacred.feature.invoicecapture.domain.FrameQualityMetrics
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.min

actual class CaptureCameraController internal constructor(
    private val imageCapture: ImageCapture,
    private val readyState: androidx.compose.runtime.State<Boolean>,
    private val capturingState: androidx.compose.runtime.MutableState<Boolean>,
    private val executor: Executor,
    private val onCapture: (ByteArray?) -> Unit,
) {
    actual val isCameraReady: Boolean get() = readyState.value
    actual val isCapturing: Boolean get() = capturingState.value

    actual fun capture() {
        if (capturingState.value || !readyState.value) return
        capturingState.value = true

        val outputStream = ByteArrayOutputStream()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()
        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    capturingState.value = false
                    onCapture(outputStream.toByteArray())
                }

                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                    capturingState.value = false
                    onCapture(null)
                }
            },
        )
    }
}

@Composable
actual fun rememberCaptureCameraController(
    isFlashEnabled: Boolean,
    onFrameMetrics: (FrameQualityMetrics) -> Unit,
    onCapture: (ByteArray?) -> Unit,
): CaptureCameraController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isReady by remember { mutableStateOf(false) }
    val isCapturing = remember { mutableStateOf(false) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    val preview = remember { Preview.Builder().build() }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    // Flash mode follows state without rebinding the whole camera session.
    LaunchedEffect(isFlashEnabled) {
        imageCapture.flashMode = if (isFlashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var lastAnalyzedAtMs = 0L

        imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
            val now = System.currentTimeMillis()
            // Throttle to ~3 analyses/sec — ML Kit text recognition is the
            // expensive part; running every frame (30fps) burns battery for
            // no real UX benefit at this update rate.
            if (now - lastAnalyzedAtMs < 300) {
                imageProxy.close()
                return@setAnalyzer
            }
            lastAnalyzedAtMs = now
            analyzeFrame(imageProxy, textRecognizer, onFrameMetrics)
        }

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            try {
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                    imageAnalysis,
                )
                isReady = true
            } catch (exc: Exception) {
                exc.printStackTrace()
                isReady = false
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProviderFuture.get().unbindAll()
            cameraExecutor.shutdown()
            analysisExecutor.shutdown()
            textRecognizer.close()
        }
    }

    return remember {
        CaptureCameraController(
            imageCapture = imageCapture,
            readyState = derivedIsReady(::isReady) { isReady },
            capturingState = isCapturing,
            executor = cameraExecutor,
            onCapture = onCapture,
        )
    }.also { controllerPreviewHolder.value = preview }
}

// Small holder so CaptureCameraPreview (a separate @Composable, called by
// the screen) can bind PreviewView.surfaceProvider to the same Preview
// use case created above, without changing the public expect signature.
private val controllerPreviewHolder = androidx.compose.runtime.mutableStateOf<Preview?>(null)

private fun derivedIsReady(unused: () -> Boolean, block: () -> Boolean) =
    androidx.compose.runtime.derivedStateOf(block)

@Composable
actual fun CaptureCameraPreview(
    controller: CaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit,
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
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
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                controllerPreviewHolder.value?.setSurfaceProvider(this.surfaceProvider)
            }
        },
        update = { previewView ->
            controllerPreviewHolder.value?.setSurfaceProvider(previewView.surfaceProvider)
        },
    )
}

// ─────────────────────────────────────────────────────────────────────────
// Frame analysis — lighting / sharpness / text density
// ─────────────────────────────────────────────────────────────────────────

private fun analyzeFrame(
    imageProxy: ImageProxy,
    textRecognizer: com.google.mlkit.vision.text.TextRecognizer,
    onFrameMetrics: (FrameQualityMetrics) -> Unit,
) {
    if (imageProxy.format != ImageFormat.YUV_420_888 || imageProxy.planes.isEmpty()) {
        imageProxy.close()
        return
    }

    val lightingScore = computeLightingScore(imageProxy)
    val sharpnessScore = computeSharpnessScore(imageProxy)

    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        onFrameMetrics(FrameQualityMetrics(lightingScore, sharpnessScore, 0f))
        return
    }

    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    textRecognizer.process(inputImage)
        .addOnSuccessListener { visionText ->
            val frameArea = (imageProxy.width * imageProxy.height).toFloat().coerceAtLeast(1f)
            val textArea = visionText.textBlocks.sumOf { block ->
                val box = block.boundingBox
                if (box != null) (box.width() * box.height()).toLong() else 0L
            }.toFloat()
            val textDensityScore = min(1f, textArea / frameArea * TEXT_DENSITY_GAIN)
            onFrameMetrics(FrameQualityMetrics(lightingScore, sharpnessScore, textDensityScore))
        }
        .addOnFailureListener {
            onFrameMetrics(FrameQualityMetrics(lightingScore, sharpnessScore, 0f))
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

/** Mean luma from the Y plane, normalized 0..1. Cheap — no bitmap conversion. */
private fun computeLightingScore(imageProxy: ImageProxy): Float {
    val yPlane = imageProxy.planes[0].buffer
    val bytes = ByteArray(yPlane.remaining())
    yPlane.get(bytes)
    var sum = 0L
    var i = 0
    while (i < bytes.size) {
        sum += (bytes[i].toInt() and 0xFF)
        i += 8 // sparse sample — a full pass isn't needed for a brightness estimate
    }
    val sampleCount = (bytes.size / 8).coerceAtLeast(1)
    return (sum.toFloat() / sampleCount / 255f).coerceIn(0f, 1f)
}

/**
 * Approximate sharpness via row-to-row luma delta variance on the Y plane —
 * a cheap stand-in for a Laplacian variance. Blurry frames have low
 * high-frequency variance; sharp, well-focused text has high variance.
 * TUNE: SHARPNESS_GAIN below controls how variance maps to the 0..1 range —
 * calibrate against real captures, this constant is a starting guess.
 */
private fun computeSharpnessScore(imageProxy: ImageProxy): Float {
    val yPlane = imageProxy.planes[0].buffer
    val rowStride = imageProxy.planes[0].rowStride
    val bytes = ByteArray(yPlane.remaining())
    yPlane.get(bytes)

    val width = imageProxy.width
    val height = imageProxy.height
    var deltaSumSq = 0.0
    var samples = 0
    var y = 8
    while (y < height - 8) {
        var x = 8
        while (x < width - 8) {
            val idx = y * rowStride + x
            val idxRight = y * rowStride + (x + 4)
            if (idxRight < bytes.size) {
                val a = bytes[idx].toInt() and 0xFF
                val b = bytes[idxRight].toInt() and 0xFF
                val delta = (a - b).toDouble()
                deltaSumSq += delta * delta
                samples++
            }
            x += 24 // sparse grid sample for speed
        }
        y += 24
    }
    if (samples == 0) return 0f
    val variance = deltaSumSq / samples
    return (variance / SHARPNESS_GAIN).toFloat().coerceIn(0f, 1f)
}

private const val TEXT_DENSITY_GAIN = 3.0f  // TUNE: raise if READY triggers too late
private const val SHARPNESS_GAIN = 900.0    // TUNE: raise if blurry frames score too high
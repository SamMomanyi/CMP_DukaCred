
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.samduka.dukacred.feature.invoicecapture.domain.FrameQualityMetrics

// AVFoundation capture — not yet implemented per architecture doc §11.
actual class CaptureCameraController {
    actual val isCameraReady: Boolean = false
    actual val isCapturing: Boolean = false
    actual fun capture() = Unit
}

@Composable
actual fun rememberCaptureCameraController(
    isFlashEnabled: Boolean,
    onFrameMetrics: (FrameQualityMetrics) -> Unit,
    onCapture: (ByteArray?) -> Unit,
): CaptureCameraController = CaptureCameraController()

@Composable
actual fun CaptureCameraPreview(
    controller: CaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize())
}
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.samduka.dukacred.feature.invoicecapture.domain.FrameQualityMetrics


// ─────────────────────────────────────────────────────────────────────────
// expect declarations — commonMain contract only. Android `actual` still
// pending: needs FrameQualityMetrics's real constructor to emit it from
// CameraX/ML Kit instead of guessed field values.
// ─────────────────────────────────────────────────────────────────────────

expect class CaptureCameraController {
    val isCameraReady: Boolean
    val isCapturing: Boolean
    fun capture()
}

@Composable
expect fun rememberCaptureCameraController(
    isFlashEnabled: Boolean,
    onFrameMetrics: (FrameQualityMetrics) -> Unit,
    onCapture: (ByteArray?) -> Unit,
): CaptureCameraController

@Composable
expect fun CaptureCameraPreview(
    controller: CaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit,
)
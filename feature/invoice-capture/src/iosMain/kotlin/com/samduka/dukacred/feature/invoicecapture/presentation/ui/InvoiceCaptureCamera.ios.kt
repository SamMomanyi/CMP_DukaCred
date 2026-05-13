package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


// PEEKABOO IMPORTS
import com.preat.peekaboo.ui.camera.CameraMode
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.PeekabooCameraState
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
actual class InvoiceCaptureCameraController(
    val state: PeekabooCameraState
) {
    actual val isCameraReady: Boolean
        get() = state.isCameraReady

    actual val isCapturing: Boolean
        get() = state.isCapturing

    actual fun capture() {
        state.capture()
    }
}

@Composable
actual fun rememberInvoiceCaptureCameraController(
    onCapture: (ByteArray?) -> Unit
): InvoiceCaptureCameraController {
    val state = rememberPeekabooCameraState(
        initialCameraMode = CameraMode.Back,
        onCapture = onCapture
    )
    return InvoiceCaptureCameraController(state)
}

@Composable
actual fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit
) {
    PeekabooCamera(
        state = controller.state,
        modifier = modifier,
        permissionDeniedContent = permissionDeniedContent
    )
}



package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.samduka.dukacred.core.designsystem.DukaCredColors

actual class InvoiceCaptureCameraController {
    actual val isCameraReady: Boolean = false
    actual val isCapturing: Boolean = false

    actual fun capture() = Unit
    actual val hasInvoiceText: Boolean
        get() = TODO("Not yet implemented")
    actual val hasAdequateLight: Boolean
        get() = TODO("Not yet implemented")
}

@Composable
actual fun rememberInvoiceCaptureCameraController(
    onCapture: (ByteArray?) -> Unit
): InvoiceCaptureCameraController = InvoiceCaptureCameraController()

@Composable
actual fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DukaCredColors.ForestGreen900),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Camera preview is unavailable on the desktop JVM target.",
            color = DukaCredColors.Cream100
        )
    }
}

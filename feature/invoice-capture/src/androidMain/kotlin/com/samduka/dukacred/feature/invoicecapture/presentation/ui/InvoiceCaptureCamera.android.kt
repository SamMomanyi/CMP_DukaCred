package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import android.Manifest
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
import androidx.core.content.ContextCompat
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
    return remember(state) { InvoiceCaptureCameraController(state) }
}

@Composable
actual fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit
) {
    val context = LocalContext.current

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
        PeekabooCamera(
            state = controller.state,
            modifier = modifier,
            permissionDeniedContent = permissionDeniedContent // Fallback just in case
        )
    } else {
        permissionDeniedContent()
    }
}
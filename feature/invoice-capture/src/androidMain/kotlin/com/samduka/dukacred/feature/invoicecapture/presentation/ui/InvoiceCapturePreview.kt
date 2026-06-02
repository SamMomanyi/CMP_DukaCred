// androidMain/.../presentation/ui/InvoiceCapturePreview.android.kt
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// ── actual remember ────────────────────────────────────────────────────────────

@Composable
actual fun rememberInvoiceCaptureCameraController(
    onCapture: (ByteArray?) -> Unit,
): InvoiceCaptureCameraController {
    val context = LocalContext.current

    // rememberUpdatedState ensures the lambda in the long-lived controller
    // always calls the most recent version of onCapture (avoids stale closures)
    val currentOnCapture by rememberUpdatedState(onCapture)

    val controller = remember {
        InvoiceCaptureCameraController(
            context   = context,
            onCapture = { bytes -> currentOnCapture(bytes) },
        )
    }

    DisposableEffect(Unit) {
        onDispose { controller.release() }
    }

    return controller
}

// ── actual preview ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit,
) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val lifecycleOwner   = LocalLifecycleOwner.current

    // Request permission on first composition if not already granted
    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    if (cameraPermission.status.isGranted) {
        AndroidView(
            modifier = modifier,
            factory  = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType          = PreviewView.ScaleType.FILL_CENTER
                }
            },
            // bindCamera is idempotent (checks boundOwner === lifecycleOwner)
            // so calling it in update on every recomposition is safe
            update   = { previewView ->
                controller.bindCamera(lifecycleOwner, previewView)
            },
        )
    } else {
        permissionDeniedContent()
    }
}
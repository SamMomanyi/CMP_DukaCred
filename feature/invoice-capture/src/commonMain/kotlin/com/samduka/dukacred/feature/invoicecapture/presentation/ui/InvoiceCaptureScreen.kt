package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samduka.dukacred.core.designsystem.DukaCredColors

@Composable
fun InvoiceCaptureScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraController = rememberInvoiceCaptureCameraController(
        onCapture = { imageBytes ->
            println("InvoiceCaptureScreen captured bytes=${imageBytes?.size ?: 0}")
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        InvoiceCapturePreview(
            controller = cameraController,
            modifier = Modifier.fillMaxSize(),
            permissionDeniedContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DukaCredColors.ForestGreen900),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Camera permission is required to capture invoices.",
                        color = DukaCredColors.Cream100
                    )
                }
            }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DukaCredColors.BlackAlpha40)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DukaCredColors.WhiteAlpha10)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close camera",
                        tint = DukaCredColors.Cream100
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    onClick = { cameraController.capture() },
                    enabled = cameraController.isCameraReady && !cameraController.isCapturing,
                    modifier = Modifier.size(92.dp),
                    shape = CircleShape,
                    color = DukaCredColors.Cream100,
                    contentColor = DukaCredColors.ForestGreen900
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (cameraController.isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = DukaCredColors.ForestGreen900,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(DukaCredColors.Ochre400)
                            )
                        }
                    }
                }
            }
        }
    }
}

expect class InvoiceCaptureCameraController {
    val isCameraReady: Boolean
    val isCapturing: Boolean
    fun capture()
}

@Composable
expect fun rememberInvoiceCaptureCameraController(
    onCapture: (ByteArray?) -> Unit
): InvoiceCaptureCameraController

@Composable
expect fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier = Modifier,
    permissionDeniedContent: @Composable () -> Unit
)

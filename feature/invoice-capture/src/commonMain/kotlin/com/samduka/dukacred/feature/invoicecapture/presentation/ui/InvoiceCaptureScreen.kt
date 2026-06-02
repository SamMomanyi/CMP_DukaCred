package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.feature.invoicecapture.sensor.rememberIsShaking
import com.samduka.dukacred.feature.invoicecapture.util.BRIGHTNESS_THRESHOLD
import com.samduka.dukacred.feature.invoicecapture.util.analyzeBrightness
import kotlinx.coroutines.delay

// ── Warning model ─────────────────────────────────────────────────────────────


// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun InvoiceCaptureScreen(
    onClose: () -> Unit,
    onImageCaptured: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeWarning by remember { mutableStateOf<CaptureWarning?>(null) }
    val isShaking by rememberIsShaking(thresholdG = 1.05f)

    LaunchedEffect(activeWarning) {
        if (activeWarning != null) {
            delay(3_000)
            activeWarning = null
        }
    }

    val cameraController = rememberInvoiceCaptureCameraController(
        onCapture = { bytes ->
            if (bytes == null) return@rememberInvoiceCaptureCameraController
            if (analyzeBrightness(bytes) < BRIGHTNESS_THRESHOLD) {
                activeWarning = CaptureWarning.LowLight
            } else {
                activeWarning = null
                onImageCaptured(bytes)
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // ── Camera preview ────────────────────────────────────────────
        InvoiceCapturePreview(
            controller = cameraController,
            modifier   = Modifier.fillMaxSize(),
            permissionDeniedContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DukaCredColors.ForestGreen900),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "Camera permission required to capture invoices.",
                        color = DukaCredColors.Cream100
                    )
                }
            }
        )

        // ── Scanner overlay ───────────────────────────────────────────
        ScannerOverlay(modifier = Modifier.fillMaxSize())

        // ── Top bar ───────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(DukaCredColors.BlackAlpha40)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick  = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DukaCredColors.WhiteAlpha10)
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Close,
                    contentDescription = "Close camera",
                    tint               = DukaCredColors.Cream100
                )
            }
        }

        // ── Warning banner ────────────────────────────────────────────
        val displayWarning = if (isShaking) CaptureWarning.Shaking else activeWarning
        AnimatedVisibility(
            visible  = displayWarning != null,
            enter    = slideInVertically { -it } + fadeIn(),
            exit     = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 72.dp, start = 20.dp, end = 20.dp)
        ) {
            val (bg, msg) = when (displayWarning) {
                CaptureWarning.LowLight -> Color(0xCC1A0000) to "⚠\uFE0F  Low light — move to a brighter area and retake"
                CaptureWarning.Shaking  -> Color(0xCCD94F4F) to "\uD83D\uDCF5  Hold phone steady"
                null                    -> Color.Transparent to ""
            }
            Surface(
                color = bg,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = msg,
                    color      = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

        // ── Capture button ────────────────────────────────────────────
        val canCapture = cameraController.isCameraReady
                && !cameraController.isCapturing
                && !isShaking

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                onClick      = { cameraController.capture() },
                enabled      = canCapture,
                modifier     = Modifier.size(88.dp),
                shape        = CircleShape,
                color        = if (canCapture) DukaCredColors.Cream100 else DukaCredColors.Cream300,
                contentColor = DukaCredColors.ForestGreen900
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (cameraController.isCapturing) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(32.dp),
                            color       = DukaCredColors.ForestGreen900,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isShaking) DukaCredColors.Error
                                    else DukaCredColors.Ochre400
                                )
                        )
                    }
                }
            }
        }
    }
}

// ── expect declarations (unchanged) ──────────────────────────────────────────

expect class InvoiceCaptureCameraController {
    val isCameraReady: Boolean
    val isCapturing: Boolean
    val hasInvoiceText: Boolean    // ← ML Kit: structured invoice text visible
    val hasAdequateLight: Boolean  // ← real-time Y-plane brightness ≥ threshold
    fun capture()
}

@Composable
expect fun rememberInvoiceCaptureCameraController(
    onCapture: (ByteArray?) -> Unit,
): InvoiceCaptureCameraController

@Composable
expect fun InvoiceCapturePreview(
    controller: InvoiceCaptureCameraController,
    modifier: Modifier,
    permissionDeniedContent: @Composable () -> Unit,
)
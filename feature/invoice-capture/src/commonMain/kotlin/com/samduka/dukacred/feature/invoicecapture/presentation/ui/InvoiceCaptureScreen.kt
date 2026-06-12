package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.DukaCredColors
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureEffect
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureIntent
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureState
import com.samduka.dukacred.feature.invoicecapture.presentation.InvoiceCaptureViewModel
import com.samduka.dukacred.feature.invoicecapture.sensor.rememberIsShaking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun InvoiceCaptureScreen(
    onClose: () -> Unit,
    onImageCaptured: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvoiceCaptureViewModel = koinViewModel(),
) {
    val uiState by viewModel.state.collectAsState()
    val isShaking by rememberIsShaking(thresholdG = 1.05f)

    // ── Camera controller ──────────────────────────────────────────────────────
// ── Camera controller ──────────────────────────────────────────────────────
    val cameraController = rememberInvoiceCaptureCameraController(
        onCapture = { bytes ->
            if (bytes == null) {
                viewModel.onIntent(InvoiceCaptureIntent.CaptureFailed)
                return@rememberInvoiceCaptureCameraController
            }
            viewModel.onIntent(InvoiceCaptureIntent.ImageCaptured(bytes))
            onImageCaptured(bytes)
        }
    )

// ── REMOVED: captureConditionsMet derivedStateOf (was duplicate trigger) ──
// ── REMOVED: LaunchedEffect(captureConditionsMet) (was duplicate trigger) ──

// ── Auto-capture trigger — single source of truth ─────────────────────────
// snapshotFlow tracks every mutableStateOf read inside the lambda, so any
// change to any of the four values re-evaluates it immediately.
// collectLatest cancels the in-flight delay if conditions drop mid-hold.
    LaunchedEffect(Unit) {
        snapshotFlow {
            cameraController.isCameraReady   &&
                    cameraController.hasInvoiceText  &&
                    cameraController.hasAdequateLight &&
                    !isShaking
        }
            .distinctUntilChanged()
            .collectLatest { conditionsMet ->
                if (conditionsMet) {
                    delay(600)
                    viewModel.onIntent(InvoiceCaptureIntent.AutoCaptureReady)
                } else {
                    viewModel.onIntent(InvoiceCaptureIntent.AutoCaptureCancelled)
                }
            }
    }

// ── Collect one-time effects ───────────────────────────────────────────────
// collectLatest is already imported; avoids the missing 'collect' import error.
// Safe here because the handler body (capture()) is non-suspending.
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect: InvoiceCaptureEffect ->
            when (effect) {
                InvoiceCaptureEffect.TriggerCapture ->
                    cameraController.capture()

                InvoiceCaptureEffect.NavigateToDashboard -> {
                    // wire NavController here
                }
            }
        }
    }

// ── "No text" warning — dedicated observer, 2 s grace period ──────────────
    var showNoTextWarning by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        snapshotFlow { cameraController.isCameraReady && !cameraController.hasInvoiceText }
            .distinctUntilChanged()
            .collectLatest { noText ->
                if (noText) {
                    delay(2_000)
                    showNoTextWarning = true   // ← was never set before, now it is
                } else {
                    showNoTextWarning = false
                }
            }
    }

    // Priority order: physical safety > image quality > guidance
    val displayWarning: CaptureWarning? = when {
        isShaking -> CaptureWarning.Shaking
        !cameraController.hasAdequateLight && cameraController.isCameraReady
            -> CaptureWarning.LowLight

        showNoTextWarning -> CaptureWarning.NoTextDetected
        else -> null
    }

    // ── Root ───────────────────────────────────────────────────────────────────
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── 1. Camera preview ──────────────────────────────────────────────────
        InvoiceCapturePreview(
            controller = cameraController,
            modifier = Modifier.fillMaxSize(),
            permissionDeniedContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DukaCredColors.ForestGreen900),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Camera permission required to capture invoices.",
                        color = DukaCredColors.Cream100,
                    )
                }
            },
        )

        // ── 2. Scanner overlay — turns green when invoice is detected ──────────
        val documentLocked = cameraController.hasInvoiceText && cameraController.hasAdequateLight
        ScannerOverlay(
            modifier = Modifier.fillMaxSize(),
            isDocumentDetected = documentLocked,
        )

        // ── 3. Top bar ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(DukaCredColors.BlackAlpha40)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DukaCredColors.WhiteAlpha10),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close camera",
                    tint = DukaCredColors.Cream100,
                )
            }
        }

        // ── 4. Warning banner ──────────────────────────────────────────────────
        AnimatedVisibility(
            visible = displayWarning != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 72.dp, start = 20.dp, end = 20.dp),
        ) {
            if (displayWarning != null) {
                WarningBanner(
                    warning = displayWarning,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // ── 5. Countdown overlay (slides over everything during AutoCapturing) ──
        AnimatedVisibility(
            visible = uiState is InvoiceCaptureState.AutoCapturing,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.fillMaxSize(),
        ) {
            val countdown = (uiState as? InvoiceCaptureState.AutoCapturing)?.countdown ?: 0
            CountdownOverlay(countdown = countdown)
        }

        // ── 6. Capture button ──────────────────────────────────────────────────
        val isScanning = uiState is InvoiceCaptureState.Scanning
        val isCounting = uiState is InvoiceCaptureState.AutoCapturing

        // Allow manual shutter in Idle and AutoCapturing;
        // disable while the camera is mid-capture or the cloud is processing
        val canCapture = cameraController.isCameraReady &&
                !cameraController.isCapturing &&
                !isScanning

        // Inner circle colour signals device state at a glance
        val innerColor by animateColorAsState(
            targetValue = when {
                isShaking -> DukaCredColors.Error
                documentLocked && !isCounting -> Color(0xFF4CAF50)  // green lock
                else -> DukaCredColors.Ochre400
            },
            animationSpec = tween(300),
            label = "inner_circle_color",
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                onClick = { viewModel.onIntent(InvoiceCaptureIntent.TakePictureClicked) },
                enabled = canCapture,
                modifier = Modifier.size(88.dp),
                shape = CircleShape,
                color = if (canCapture) DukaCredColors.Cream100 else DukaCredColors.Cream300,
                contentColor = DukaCredColors.ForestGreen900,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when {
                        cameraController.isCapturing || isScanning -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = DukaCredColors.ForestGreen900,
                                strokeWidth = 3.dp,
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(innerColor),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Countdown overlay composable (private to this screen) ─────────────────────

@Composable
private fun CountdownOverlay(countdown: Int) {
    // reset scale to 0 on each new number, then spring-animate to 1
    var targetScale by remember(countdown) { mutableStateOf(0f) }
    LaunchedEffect(countdown) { targetScale = 1f }

    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "countdown_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)),   // dim-but-see-through scrim
        contentAlignment = Alignment.Center,
    ) {
        // Pulsing circle
        Box(
            modifier = Modifier
                .size(128.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(CircleShape)
                .background(DukaCredColors.ForestGreen900.copy(alpha = 0.92f))
                .border(3.dp, DukaCredColors.Ochre400, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$countdown",
                color = DukaCredColors.Cream100,
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 56.sp,
            )
        }

        // Guidance label at the bottom
        Text(
            text = "Hold steady…",
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 160.dp),
        )
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
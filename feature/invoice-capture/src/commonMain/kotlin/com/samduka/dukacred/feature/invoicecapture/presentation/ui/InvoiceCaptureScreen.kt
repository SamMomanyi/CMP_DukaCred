package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.draw.clip
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.feature.invoicecapture.presentation.*
import com.samduka.dukacred.feature.invoicecapture.sensor.rememberIsShaking
import kotlinx.coroutines.delay
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
    viewModel: InvoiceCaptureViewModel = koinViewModel(),
) {
    val uiState by viewModel.state.collectAsState()
    val isShaking by rememberIsShaking(thresholdG = 1.05f)

    // ── Camera controller ──────────────────────────────────────────────────────
    val cameraController = rememberInvoiceCaptureCameraController(
        onCapture = { bytes ->
            if (bytes == null) {
                viewModel.onIntent(InvoiceCaptureIntent.CaptureFailed)
                return@rememberInvoiceCaptureCameraController
            }
            // Brightness was validated in real-time via hasAdequateLight,
            // so forward the bytes directly without a post-capture re-check
            viewModel.onIntent(InvoiceCaptureIntent.ImageCaptured(bytes))
            onImageCaptured(bytes)
        }
    )

    // ── Auto-capture gate — all three conditions must be true ──────────────────
    val captureConditionsMet by remember {
        derivedStateOf {
            cameraController.isCameraReady   &&
                    cameraController.hasInvoiceText  &&
                    cameraController.hasAdequateLight &&
                    !isShaking
        }
    }

    // Debounce: conditions must hold for 500 ms before we start the countdown.
    // LaunchedEffect cancels automatically when captureConditionsMet flips,
    // so rapid oscillations (common when panning the camera) never trigger a capture.
    LaunchedEffect(captureConditionsMet) {
        if (captureConditionsMet) {
            delay(500)
            if (captureConditionsMet) viewModel.onIntent(InvoiceCaptureIntent.AutoCaptureReady)
        } else {
            viewModel.onIntent(InvoiceCaptureIntent.AutoCaptureCancelled)
        }
    }

    // ── Collect one-time effects ───────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                InvoiceCaptureEffect.TriggerCapture      -> cameraController.capture()
                InvoiceCaptureEffect.NavigateToDashboard -> { /* hook your NavController here */ }
            }
        }
    }

    // ── Deferred "no text" warning — 2s grace period so it doesn't flash ───────
    var showNoTextWarning by remember { mutableStateOf(false) }
    LaunchedEffect(cameraController.isCameraReady, cameraController.hasInvoiceText) {
        if (cameraController.isCameraReady && !cameraController.hasInvoiceText) {
            delay(2_000)
            showNoTextWarning = !cameraController.hasInvoiceText
        } else {
            showNoTextWarning = false
        }
    }

    // Priority order: physical safety > image quality > guidance
    val displayWarning: CaptureWarning? = when {
        isShaking                                                   -> CaptureWarning.Shaking
        !cameraController.hasAdequateLight && cameraController.isCameraReady
            -> CaptureWarning.LowLight
        showNoTextWarning                                           -> CaptureWarning.NoTextDetected
        else                                                        -> null
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
            modifier   = Modifier.fillMaxSize(),
            permissionDeniedContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DukaCredColors.ForestGreen900),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = "Camera permission required to capture invoices.",
                        color = DukaCredColors.Cream100,
                    )
                }
            },
        )

        // ── 2. Scanner overlay — turns green when invoice is detected ──────────
        val documentLocked = cameraController.hasInvoiceText && cameraController.hasAdequateLight
        ScannerOverlay(
            modifier           = Modifier.fillMaxSize(),
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
                onClick  = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DukaCredColors.WhiteAlpha10),
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Close,
                    contentDescription = "Close camera",
                    tint               = DukaCredColors.Cream100,
                )
            }
        }

        // ── 4. Warning banner ──────────────────────────────────────────────────
        AnimatedVisibility(
            visible  = displayWarning != null,
            enter    = slideInVertically { -it } + fadeIn(),
            exit     = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 72.dp, start = 20.dp, end = 20.dp),
        ) {
            if (displayWarning != null) {
                WarningBanner(
                    warning  = displayWarning,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // ── 5. Countdown overlay (slides over everything during AutoCapturing) ──
        AnimatedVisibility(
            visible  = uiState is InvoiceCaptureState.AutoCapturing,
            enter    = fadeIn(tween(200)),
            exit     = fadeOut(tween(200)),
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
                !cameraController.isCapturing         &&
                !isScanning

        // Inner circle colour signals device state at a glance
        val innerColor by animateColorAsState(
            targetValue = when {
                isShaking    -> DukaCredColors.Error
                documentLocked && !isCounting -> Color(0xFF4CAF50)  // green lock
                else         -> DukaCredColors.Ochre400
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
                onClick      = { viewModel.onIntent(InvoiceCaptureIntent.TakePictureClicked) },
                enabled      = canCapture,
                modifier     = Modifier.size(88.dp),
                shape        = CircleShape,
                color        = if (canCapture) DukaCredColors.Cream100 else DukaCredColors.Cream300,
                contentColor = DukaCredColors.ForestGreen900,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when {
                        cameraController.isCapturing || isScanning -> {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(32.dp),
                                color       = DukaCredColors.ForestGreen900,
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
        targetValue   = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMediumLow,
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
                text       = "$countdown",
                color      = DukaCredColors.Cream100,
                fontSize   = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 56.sp,
            )
        }

        // Guidance label at the bottom
        Text(
            text       = "Hold steady…",
            color      = Color.White.copy(alpha = 0.80f),
            fontSize   = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier
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
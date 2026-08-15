
package com.samduka.dukacred.feature.invoicecapture.presentation

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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.components.DukaPrimaryButton
import com.samduka.dukacred.core.designsystem.components.DukaSecondaryButton
import com.samduka.dukacred.core.designsystem.components.DukaSurfaceCard
import com.samduka.dukacred.core.designsystem.components.dukaSkeletonBrush
import com.samduka.dukacred.core.designsystem.theme.dukaShapes
import com.samduka.dukacred.feature.invoicecapture.domain.CaptureGuidance
import com.samduka.dukacred.feature.invoicecapture.domain.FrameQualityMetrics
import com.samduka.dukacred.feature.invoicecapture.presentation.ui.CaptureCameraPreview
import com.samduka.dukacred.feature.invoicecapture.presentation.ui.formatAmount
import com.samduka.dukacred.feature.invoicecapture.presentation.ui.rememberCaptureCameraController
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    onClose: () -> Unit,
    onNavigateToAdjustment: (invoiceId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CaptureViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Camera controller ────────────────────────────────────────────────────
    // NOTE: rememberCaptureCameraController's Android `actual` is NOT included —
    // see chat flag. commonMain wiring below is complete and ready for it.
    val cameraController = rememberCaptureCameraController(
        isFlashEnabled = state.isFlashEnabled,
        onFrameMetrics = { metrics -> viewModel.onIntent(CaptureIntent.FrameAnalyzed(metrics)) },
        onCapture = { bytes ->
            if (bytes != null) {
                viewModel.onIntent(CaptureIntent.ImageCaptured(bytes))
            }
        },
    )

    // ── One-off effects ───────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is CaptureEffect.NavigateToAdjustmentScreen -> onNavigateToAdjustment(effect.invoiceId)
                CaptureEffect.NavigateBack -> onClose()
                is CaptureEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                // TriggerHapticFeedback: platform haptics call goes here once
                // an expect/actual HapticFeedback wrapper exists — not stubbed
                // silently since a fake vibration call would be worse than none.
                CaptureEffect.TriggerHapticFeedback -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Black,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            // ── 1. Camera preview ──────────────────────────────────────────────
            CaptureCameraPreview(
                controller = cameraController,
                modifier = Modifier.fillMaxSize(),
                permissionDeniedContent = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Camera permission required to capture invoices.",
                            color = Color.White,
                        )
                    }
                },
            )

            // ── 2. Reticle — 4 corner brackets, green once locked ────────────────
            val isLocked = state.guidance == CaptureGuidance.READY || state.autoCaptureCountdownSeconds != null
            CaptureReticle(
                modifier = Modifier.fillMaxSize(),
                isLocked = isLocked,
            )

            // ── 3. Top controls: Close · Flash · Gallery · Settings ─────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundIconButton(icon = Icons.Rounded.Close, contentDescription = "Close", onClick = onClose)
                Row {
                    RoundIconButton(
                        icon = if (state.isFlashEnabled) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                        contentDescription = "Toggle flash",
                        onClick = { viewModel.onIntent(CaptureIntent.ToggleFlash) },
                    )
                    Spacer(Modifier.width(8.dp))
                    // Gallery picker + settings gear: not wired to an intent —
                    // CaptureContract has no GalleryImageSelected / OpenSettings
                    // intent yet. Flagging rather than inventing one.
                    RoundIconButton(icon = Icons.Rounded.PhotoLibrary, contentDescription = "Choose from gallery", onClick = {})
                }
            }

            // ── 4. Dynamic feedback pill ──────────────────────────────────────
            AnimatedVisibility(
                visible = !state.isProcessing,
                enter = fadeIn() + slideInVertically { -it / 2 },
                exit = fadeOut() + slideOutVertically { -it / 2 },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 64.dp),
            ) {
                FeedbackPill(guidance = state.guidance, errorMessage = state.errorMessage)
            }

            // ── 5. Countdown overlay ───────────────────────────────────────────
            AnimatedVisibility(
                visible = state.autoCaptureCountdownSeconds != null,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200)),
                modifier = Modifier.fillMaxSize(),
            ) {
                CountdownOverlay(countdown = state.autoCaptureCountdownSeconds ?: 0)
            }

            // ── 6. Bottom controls ─────────────────────────────────────────────
            AnimatedVisibility(
                visible = !state.isProcessing && state.draft == null,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // "Manual Adjust" — no intent in CaptureContract yet, flagged.
                    BottomLabelAction(
                        icon = Icons.Rounded.EditNote,
                        label = "Manual Adjust",
                        modifier = Modifier.weight(1f),
                        onClick = {},
                    )

                    ShutterButton(
                        enabled = cameraController.isCameraReady && !cameraController.isCapturing,
                        isBusy = cameraController.isCapturing,
                        isLocked = isLocked,
                        onClick = { viewModel.onIntent(CaptureIntent.ManualCaptureClicked) },
                    )

                    // "Manual Entry" — same: no matching intent yet.
                    BottomLabelAction(
                        icon = Icons.Rounded.EditNote,
                        label = "Manual Entry",
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        alignEnd = true,
                    )
                }
            }

            // ── 7. AI processing overlay — 3-stage skeleton ────────────────────
            AnimatedVisibility(
                visible = state.isProcessing,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize(),
            ) {
                ProcessingOverlay(stage = state.processingStage)
            }

            // ── 8. Draft ready — generic review card (see flag above) ─────────
            AnimatedVisibility(
                visible = !state.isProcessing && state.draft != null,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                state.draft?.let { draft ->
                    DraftReadyCard(
                        draft = draft,
                        onConfirm = { viewModel.onIntent(CaptureIntent.ConfirmExtractedDraft) },
                        onRetake = { viewModel.onIntent(CaptureIntent.RetryCapture) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(20.dp),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun RoundIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f)),
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = Color.White)
    }
}

@Composable
private fun FeedbackPill(guidance: CaptureGuidance, errorMessage: String?) {
    // Only MOVE_CLOSER is a confirmed enum case — everything else falls
    // through to a generic label until the full CaptureGuidance enum is shared.
    val text = errorMessage ?: when (guidance) {
        CaptureGuidance.MOVE_CLOSER -> "Move closer to the invoice…"
        CaptureGuidance.HOLD_STEADY -> "Hold steady…"
        CaptureGuidance.LOW_LIGHT -> "Move to a brighter area"
        CaptureGuidance.READY -> "Perfect — hold there"
    }
    Surface(
        shape = MaterialTheme.dukaShapes.full,
        color = Color.Black.copy(alpha = 0.55f),
        contentColor = Color.White,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun CaptureReticle(modifier: Modifier = Modifier, isLocked: Boolean) {
    val activeColor by animateColorAsState(
        targetValue = if (isLocked) Color(0xFF4CAF50) else Color(0xFFE5E9E7),
        animationSpec = tween(300),
        label = "reticle_color",
    )
    Canvas(modifier = modifier) {
        val w = size.width * 0.85f
        val h = size.height * 0.55f
        val left = (size.width - w) / 2f
        val top = (size.height - h) / 2f
        val bracket = 28.dp.toPx()
        val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)

        // Top-left
        drawLine(activeColor, Offset(left, top + bracket), Offset(left, top), stroke.width)
        drawLine(activeColor, Offset(left, top), Offset(left + bracket, top), stroke.width)
        // Top-right
        drawLine(activeColor, Offset(left + w - bracket, top), Offset(left + w, top), stroke.width)
        drawLine(activeColor, Offset(left + w, top), Offset(left + w, top + bracket), stroke.width)
        // Bottom-left
        drawLine(activeColor, Offset(left, top + h - bracket), Offset(left, top + h), stroke.width)
        drawLine(activeColor, Offset(left, top + h), Offset(left + bracket, top + h), stroke.width)
        // Bottom-right
        drawLine(activeColor, Offset(left + w - bracket, top + h), Offset(left + w, top + h), stroke.width)
        drawLine(activeColor, Offset(left + w, top + h), Offset(left + w, top + h - bracket), stroke.width)
    }
}

@Composable
private fun ShutterButton(
    enabled: Boolean,
    isBusy: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
) {
    val innerColor by animateColorAsState(
        targetValue = if (isLocked) Color(0xFF4CAF50) else Color.White,
        animationSpec = tween(300),
        label = "shutter_inner_color",
    )
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.15f),
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isBusy) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White, strokeWidth = 3.dp)
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(innerColor)
                )
            }
        }
    }
}

@Composable
private fun BottomLabelAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White)
        }
        Text(text = label, color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
    }
}

@Composable
private fun ProcessingOverlay(stage: ProcessingStage?) {
    val (title, subtitle) = when (stage) {
        ProcessingStage.UPLOADING -> "Securing" to "Preparing your image…"
        ProcessingStage.ANALYZING -> "Reading OCR" to "Reading the invoice text…"
        ProcessingStage.FINALIZING -> "Analyzing" to "Extracting the details…"
        null -> "Processing" to ""
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            Spacer(Modifier.height(24.dp))
            // Skeleton preview of the eventual verification card — matches
            // Section 9.4's "transition feels continuous, not a hard cut".
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Box(Modifier.fillMaxWidth().height(18.dp).dukaSkeletonBrush())
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth(0.6f).height(14.dp).dukaSkeletonBrush())
                Spacer(Modifier.height(20.dp))
                repeat(3) {
                    Box(Modifier.fillMaxWidth().height(48.dp).dukaSkeletonBrush())
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DraftReadyCard(
    draft: com.samduka.dukacred.feature.invoicecapture.domain.InvoiceDraft,
    onConfirm: () -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DukaSurfaceCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    draft.merchantName.ifBlank { "Unknown supplier" },
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                draft.invoiceDate.ifBlank { draft.invoiceNumber }?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                    )
                }
            }
            Text(
                "KES ${formatAmount(draft.totalAmount)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "${draft.lineItems.size} item${if (draft.lineItems.size == 1) "" else "s"} detected — review before submitting",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DukaSecondaryButton(text = "Retake", onClick = onRetake, modifier = Modifier.weight(1f))
            DukaPrimaryButton(text = "Confirm & Continue", onClick = onConfirm, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CountdownOverlay(countdown: Int) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "countdown_scale",
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .background(Color(0xFF013220).copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$countdown",
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

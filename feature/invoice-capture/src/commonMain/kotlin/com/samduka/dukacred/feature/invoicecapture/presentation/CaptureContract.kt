package com.samduka.dukacred.feature.invoicecapture.presentation

import com.samduka.dukacred.feature.invoicecapture.domain.CaptureGuidance
import com.samduka.dukacred.feature.invoicecapture.domain.FrameQualityMetrics
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceDraft

/** Complete immutable UI model for the smart invoice capture flow. */
data class CaptureState(
    val isCameraPreviewActive: Boolean = true,
    val guidance: CaptureGuidance = CaptureGuidance.MOVE_CLOSER,
    val autoCaptureCountdownSeconds: Int? = null,
    val isFlashEnabled: Boolean = false,
    val processingStage: ProcessingStage? = null,
    val errorMessage: String? = null,
    val draft: InvoiceDraft? = null,
    internal val compressedImage: ByteArray? = null,
) {
    val isProcessing: Boolean get() = processingStage != null
}

enum class ProcessingStage { UPLOADING, ANALYZING, FINALIZING }

sealed interface CaptureIntent {
    data object ManualCaptureClicked : CaptureIntent
    data class FrameAnalyzed(val metrics: FrameQualityMetrics) : CaptureIntent
    data object AutoCaptureTriggered : CaptureIntent
    data class ImageCaptured(val bytes: ByteArray) : CaptureIntent
    data object RetryCapture : CaptureIntent
    data object CancelProcessing : CaptureIntent
    data object ConfirmExtractedDraft : CaptureIntent
    data object ToggleFlash : CaptureIntent
}

sealed interface CaptureEffect {
    data class NavigateToAdjustmentScreen(val invoiceId: String) : CaptureEffect
    data object NavigateBack : CaptureEffect
    data class ShowSnackbar(val message: String) : CaptureEffect
    data object TriggerHapticFeedback : CaptureEffect
}

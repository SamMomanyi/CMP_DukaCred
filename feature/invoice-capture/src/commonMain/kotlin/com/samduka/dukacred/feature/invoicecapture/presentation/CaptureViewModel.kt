package com.samduka.dukacred.feature.invoicecapture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samduka.dukacred.feature.invoicecapture.domain.CaptureGuidance
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.AnalyzeFrameQualityUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.CompressImageUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.ExtractInvoiceDataUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.SaveInvoiceDraftUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * MVI coordinator for capture, Gemini extraction, and draft persistence.
 *
 * It deliberately depends only on use cases and a dispatcher; repository and
 * network details are contained in the domain/data layers.
 */
class CaptureViewModel(
    private val analyzeFrameQuality: AnalyzeFrameQualityUseCase,
    private val compressImage: CompressImageUseCase,
    private val extractInvoiceData: ExtractInvoiceDataUseCase,
    private val saveInvoiceDraft: SaveInvoiceDraftUseCase,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _state = MutableStateFlow(CaptureState())
    val state = _state.asStateFlow()

    private val effectsChannel = Channel<CaptureEffect>(Channel.BUFFERED)
    val effects: Flow<CaptureEffect> = effectsChannel.receiveAsFlow()

    private var countdownJob: Job? = null
    private var processingJob: Job? = null

    fun onIntent(intent: CaptureIntent) {
        when (intent) {
            CaptureIntent.ManualCaptureClicked -> requestCapture()
            is CaptureIntent.FrameAnalyzed -> handleFrameAnalysis(intent)
            CaptureIntent.AutoCaptureTriggered -> startAutoCaptureCountdown()
            is CaptureIntent.ImageCaptured -> analyzeCapture(intent.bytes)
            CaptureIntent.RetryCapture -> resetForRetry()
            CaptureIntent.CancelProcessing -> cancelProcessing()
            CaptureIntent.ConfirmExtractedDraft -> saveDraft()
            CaptureIntent.ToggleFlash -> _state.value = _state.value.copy(isFlashEnabled = !_state.value.isFlashEnabled)
        }
    }

    private fun handleFrameAnalysis(intent: CaptureIntent.FrameAnalyzed) {
        if (_state.value.isProcessing || _state.value.draft != null) return
        analyzeFrameQuality(intent.metrics)
            .onSuccess { result ->
                _state.value = _state.value.copy(guidance = result.guidance, errorMessage = null)
                if (result.shouldAutoCapture) startAutoCaptureCountdown() else cancelCountdown()
            }
            .onFailure { error -> _state.value = _state.value.copy(errorMessage = error.userMessage()) }
    }

    private fun startAutoCaptureCountdown() {
        if (countdownJob?.isActive == true || _state.value.isProcessing || _state.value.draft != null) return
        countdownJob = viewModelScope.launch(dispatcher) {
            for (seconds in AUTO_CAPTURE_SECONDS downTo 1) {
                _state.value = _state.value.copy(autoCaptureCountdownSeconds = seconds)
                delay(1_000)
            }
            _state.value = _state.value.copy(autoCaptureCountdownSeconds = null)
            requestCapture()
        }
    }

    private fun requestCapture() {
        cancelCountdown()
        if (_state.value.isProcessing) return
        viewModelScope.launch { effectsChannel.send(CaptureEffect.TriggerHapticFeedback) }
    }

    private fun analyzeCapture(rawBytes: ByteArray) {
        cancelCountdown()
        processingJob?.cancel()
        processingJob = viewModelScope.launch(dispatcher) {
            _state.value = _state.value.copy(processingStage = ProcessingStage.ANALYZING, errorMessage = null)
            val compressed = compressImage(rawBytes).getOrElse { error -> return@launch fail(error) }
            val draft = runCatching {
                withTimeout(EXTRACTION_TIMEOUT_MS) { extractInvoiceData(compressed).getOrThrow() }
            }.getOrElse { error -> return@launch fail(error) }
            _state.value = _state.value.copy(
                isCameraPreviewActive = false,
                processingStage = null,
                draft = draft,
                compressedImage = compressed,
            )
        }
    }

    private fun saveDraft() {
        val current = _state.value
        val draft = current.draft ?: return
        val image = current.compressedImage ?: return fail(IllegalStateException("Captured image is unavailable"))
        processingJob?.cancel()
        processingJob = viewModelScope.launch(dispatcher) {
            _state.value = current.copy(processingStage = ProcessingStage.UPLOADING, errorMessage = null)
            val saved = saveInvoiceDraft(draft, image).getOrElse { error -> return@launch fail(error) }
            _state.value = _state.value.copy(processingStage = ProcessingStage.FINALIZING)
            val invoiceId = saved.id ?: return@launch fail(IllegalStateException("Supabase did not return an invoice id"))
            effectsChannel.send(CaptureEffect.NavigateToAdjustmentScreen(invoiceId))
        }
    }

    private fun cancelProcessing() {
        processingJob?.cancel()
        processingJob = null
        resetForRetry()
        viewModelScope.launch { effectsChannel.send(CaptureEffect.ShowSnackbar("Processing cancelled")) }
    }

    private fun resetForRetry() {
        cancelCountdown()
        _state.value = CaptureState(isFlashEnabled = _state.value.isFlashEnabled)
    }

    private fun cancelCountdown() {
        countdownJob?.cancel()
        countdownJob = null
        if (_state.value.autoCaptureCountdownSeconds != null) {
            _state.value = _state.value.copy(autoCaptureCountdownSeconds = null)
        }
    }

    private fun fail(error: Throwable) {
        _state.value = _state.value.copy(processingStage = null, errorMessage = error.userMessage())
        viewModelScope.launch { effectsChannel.send(CaptureEffect.ShowSnackbar(error.userMessage())) }
    }

    private fun Throwable.userMessage(): String = message ?: "We couldn't process this invoice. Please try again."

    override fun onCleared() {
        countdownJob?.cancel()
        processingJob?.cancel()
        effectsChannel.close()
    }

    private companion object {
        const val AUTO_CAPTURE_SECONDS = 3
        const val EXTRACTION_TIMEOUT_MS = 45_000L
    }
}

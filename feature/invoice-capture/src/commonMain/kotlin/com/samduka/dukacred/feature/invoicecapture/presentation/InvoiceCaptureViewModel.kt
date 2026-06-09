// commonMain/.../presentation/InvoiceCaptureViewModel.kt
package com.samduka.dukacred.feature.invoicecapture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InvoiceCaptureViewModel : ViewModel() {

    private val _state = MutableStateFlow<InvoiceCaptureState>(InvoiceCaptureState.Idle)
    val state = _state.asStateFlow()

    private val _effect = Channel<InvoiceCaptureEffect>(Channel.BUFFERED)
    val effect: receiveAsFlow = _effect.receiveAsFlow()

    private var countdownJob: Job? = null

    // ── Intent router ──────────────────────────────────────────────────────────
    fun onIntent(intent: InvoiceCaptureIntent) {
        when (intent) {
            InvoiceCaptureIntent.AutoCaptureReady      -> startCountdown()
            InvoiceCaptureIntent.AutoCaptureCancelled  -> cancelCountdown()
            InvoiceCaptureIntent.TakePictureClicked    -> triggerManualCapture()
            is InvoiceCaptureIntent.ImageCaptured      -> handleImageCaptured(intent.imageBytes)
            InvoiceCaptureIntent.CaptureFailed         -> resetToIdle()
            InvoiceCaptureIntent.RetakeClicked         -> resetToIdle()
            InvoiceCaptureIntent.ConfirmInvoiceClicked -> navigateToDashboard()
        }
    }

    // ── Auto-capture ───────────────────────────────────────────────────────────

    private fun startCountdown() {
        // Guard: only start from Idle — prevents restarting a live countdown
        if (_state.value !is InvoiceCaptureState.Idle) return
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (tick in 3 downTo 1) {
                _state.value = InvoiceCaptureState.AutoCapturing(tick)
                delay(1_000)
            }
            // Countdown complete — hand off to the camera
            _state.value = InvoiceCaptureState.Scanning
            _effect.trySend(InvoiceCaptureEffect.TriggerCapture)
        }
    }

    private fun cancelCountdown() {
        if (_state.value is InvoiceCaptureState.AutoCapturing) {
            countdownJob?.cancel()
            _state.value = InvoiceCaptureState.Idle
        }
    }

    // ── Manual capture ─────────────────────────────────────────────────────────
    private fun triggerManualCapture() {
        countdownJob?.cancel()
        viewModelScope.launch {
            _state.value = InvoiceCaptureState.Scanning
            _effect.trySend(InvoiceCaptureEffect.TriggerCapture)
        }
    }

    // ── Post-capture ───────────────────────────────────────────────────────────
    private fun handleImageCaptured(bytes: ByteArray) {
        countdownJob?.cancel()
        _state.value = InvoiceCaptureState.Scanning
        // TODO: viewModelScope.launch { repository.processAndSaveInvoice(bytes) }
    }

    private fun resetToIdle() {
        countdownJob?.cancel()
        _state.value = InvoiceCaptureState.Idle
    }

    private fun navigateToDashboard() {
        viewModelScope.launch { _effect.trySend(InvoiceCaptureEffect.NavigateToDashboard) }
    }
}
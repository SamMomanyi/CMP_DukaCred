package com.samduka.dukacred.feature.invoicecapture.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The brain of the OCR scanning screen.
 * Listens to user intents, updates the UI state, and fires one-time effects.
 */
class InvoiceCaptureViewModel : ViewModel() {

    // 1. The Single Source of Truth for the UI
    private val _state = MutableStateFlow<InvoiceCaptureState>(InvoiceCaptureState.Idle)
    val state = _state.asStateFlow()

    // 2. The One-Time Event Channel (Navigations, Toasts)
    private val _effect = MutableSharedFlow<InvoiceCaptureEffect>()
    val effect = _effect.asSharedFlow()

    // 3. The specific actions the UI can tell us to do
    fun onIntent(intent: InvoiceCaptureIntent) {
        when (intent) {
            is InvoiceCaptureIntent.TakePictureClicked -> {
                // TODO: Trigger camera launch
            }
            is InvoiceCaptureIntent.ImageCaptured -> {
                _state.value = InvoiceCaptureState.Scanning
                // TODO: Send imageBytes to the OCR Repository
            }
            is InvoiceCaptureIntent.RetakeClicked -> {
                _state.value = InvoiceCaptureState.Idle
            }
            is InvoiceCaptureIntent.ConfirmInvoiceClicked -> {
                // TODO: Save to DB and trigger NavigateToDashboard effect
            }
        }
    }
}
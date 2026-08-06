package com.samduka.dukacred.feature.invoicecapture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceImageCache
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceValidator
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class InvoiceProcessingViewModel(
    private val imageCache: InvoiceImageCache,
    private val repository: InvoiceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<InvoiceProcessingState>(InvoiceProcessingState.Loading)
    val state = _state.asStateFlow()

    private val _effect = Channel<InvoiceProcessingEffect>(Channel.BUFFERED)
    val effect: Flow<InvoiceProcessingEffect> = _effect.receiveAsFlow()

    init {
        extract()
    }

    fun onIntent(intent: InvoiceProcessingIntent) {
        when (intent) {
            InvoiceProcessingIntent.Retry -> extract()

            is InvoiceProcessingIntent.MerchantNameChanged ->
                updateInvoice { it.copy(merchantName = intent.value) }
            is InvoiceProcessingIntent.InvoiceDateChanged ->
                updateInvoice { it.copy(invoiceDate = intent.value) }
            is InvoiceProcessingIntent.InvoiceNumberChanged ->
                updateInvoice { it.copy(invoiceNumber = intent.value) }
            is InvoiceProcessingIntent.TotalAmountChanged ->
                updateInvoice { it.copy(totalAmount = intent.value) }
            is InvoiceProcessingIntent.TaxAmountChanged ->
                updateInvoice { it.copy(taxAmount = intent.value) }

            is InvoiceProcessingIntent.LineItemChanged -> updateInvoice { invoice ->
                invoice.copy(
                    lineItems = invoice.lineItems.map { existing ->
                        if (existing.localId == intent.lineItem.localId) intent.lineItem else existing
                    }
                )
            }
            is InvoiceProcessingIntent.LineItemRemoved -> updateInvoice { invoice ->
                invoice.copy(lineItems = invoice.lineItems.filterNot { it.localId == intent.localId })
            }
            InvoiceProcessingIntent.LineItemAdded -> updateInvoice { invoice ->
                invoice.copy(lineItems = invoice.lineItems + InvoiceLineItem(description = ""))
            }

            InvoiceProcessingIntent.ConfirmAndSaveClicked -> confirmAndSave()
            InvoiceProcessingIntent.RetakeClicked -> retake()
        }
    }

    private fun extract() {
        val bytes = imageCache.capturedImageBytes
        if (bytes == null) {
            _state.value = InvoiceProcessingState.Error(
                IllegalStateException("No captured image found in cache — please retake.")
            )
            return
        }
        viewModelScope.launch {
            _state.value = InvoiceProcessingState.ExtractingAI
            repository.extractInvoice(bytes)
                .onSuccess { invoice ->
                    _state.value = InvoiceProcessingState.Success(
                        invoice = invoice,
                        validation = InvoiceValidator.validate(invoice.totalAmount, invoice.lineItems),
                    )
                }
                .onFailure { throwable ->
                    _state.value = InvoiceProcessingState.Error(throwable)
                }
        }
    }

    // No-ops outside Success — edits only make sense once there's something
    // on screen to edit.
    private fun updateInvoice(transform: (ParsedInvoice) -> ParsedInvoice) {
        val current = _state.value as? InvoiceProcessingState.Success ?: return
        val updated = transform(current.invoice)
        _state.value = current.copy(
            invoice = updated,
            validation = InvoiceValidator.validate(updated.totalAmount, updated.lineItems),
        )
    }

    private fun confirmAndSave() {
        val current = _state.value as? InvoiceProcessingState.Success ?: return
        val bytes = imageCache.capturedImageBytes
        if (bytes == null) {
            viewModelScope.launch {
                _effect.send(InvoiceProcessingEffect.ShowError("Original image is missing — please retake."))
            }
            return
        }
        viewModelScope.launch {
            _state.value = current.copy(isSaving = true)
            repository.confirmAndSaveInvoice(current.invoice, bytes)
                .onSuccess {
                    imageCache.capturedImageBytes = null
                    _effect.send(InvoiceProcessingEffect.NavigateToDashboard)
                }
                .onFailure { throwable ->
                    // Keep the merchant's edits on screen — a failed save
                    // should never wipe out work they've already done.
                    _state.value = current.copy(isSaving = false)
                    _effect.send(
                        InvoiceProcessingEffect.ShowError(
                            throwable.message ?: "Couldn't save invoice — check your connection and try again."
                        )
                    )
                }
        }
    }

    private fun retake() {
        imageCache.capturedImageBytes = null
        _state.value = InvoiceProcessingState.ReScanRequested
        viewModelScope.launch { _effect.send(InvoiceProcessingEffect.NavigateToCapture) }
    }
}
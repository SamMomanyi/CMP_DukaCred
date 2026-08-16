// presentation/ManualInvoiceEditViewModel.kt
package com.samduka.dukacred.feature.invoicecapture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samduka.dukacred.core.common.result.onFailure
import com.samduka.dukacred.core.common.result.onSuccess
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.GetInvoiceByIdUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.UpdateInvoiceDraftUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ManualInvoiceEditViewModel(
    private val invoiceId: String,
    private val getInvoiceById: GetInvoiceByIdUseCase,
    private val updateInvoiceDraft: UpdateInvoiceDraftUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ManualInvoiceEditState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ManualInvoiceEditEffect>(Channel.BUFFERED)
    val effect: Flow<ManualInvoiceEditEffect> = _effect.receiveAsFlow()

    init { load() }

    fun onIntent(intent: ManualInvoiceEditIntent) {
        when (intent) {
            ManualInvoiceEditIntent.Retry -> load()
            ManualInvoiceEditIntent.DiscardClicked -> sendEffect(ManualInvoiceEditEffect.NavigateBack)
            ManualInvoiceEditIntent.SaveClicked -> save()

            is ManualInvoiceEditIntent.MerchantNameChanged -> update { it.copy(merchantName = intent.value) }
            is ManualInvoiceEditIntent.InvoiceDateChanged -> update { it.copy(invoiceDate = intent.value) }
            is ManualInvoiceEditIntent.InvoiceNumberChanged -> update { it.copy(invoiceNumber = intent.value) }
            is ManualInvoiceEditIntent.TotalAmountChanged -> update { it.copy(totalAmount = intent.value) }

            is ManualInvoiceEditIntent.LineItemChanged -> update { invoice ->
                invoice.copy(
                    lineItems = invoice.lineItems.map { existing ->
                        if (existing.localId == intent.lineItem.localId) intent.lineItem else existing
                    }
                )
            }
            is ManualInvoiceEditIntent.LineItemRemoved -> update { invoice ->
                invoice.copy(lineItems = invoice.lineItems.filterNot { it.localId == intent.localId })
            }
            ManualInvoiceEditIntent.LineItemAdded -> update { invoice ->
                invoice.copy(lineItems = invoice.lineItems + InvoiceLineItem(description = ""))
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = ManualInvoiceEditState(isLoading = true)
            getInvoiceById(invoiceId)
                .onSuccess { invoice -> _state.value = ManualInvoiceEditState(invoice = invoice, isLoading = false) }
                .onFailure { error ->
                    _state.value = ManualInvoiceEditState(
                        isLoading = false,
                        errorMessage = error.message ?: "Couldn't load this invoice.",
                    )
                }
        }
    }

    private fun update(transform: (com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice) -> com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice) {
        val current = _state.value.invoice ?: return
        _state.value = _state.value.copy(invoice = transform(current))
    }

    private fun save() {
        val invoice = _state.value.invoice ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            updateInvoiceDraft(invoice)
                .onSuccess {
                    // NOTE: InvoiceReviewScreen (the screen we came from) has no
                    // way to know an edit happened — no nav-result plumbing yet.
                    // It'll show stale data until its own Retry/re-evaluate is
                    // triggered some other way. Flagging rather than faking it.
                    _effect.send(ManualInvoiceEditEffect.NavigateBack)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(isSaving = false)
                    _effect.send(
                        ManualInvoiceEditEffect.ShowError(error.message ?: "Couldn't save changes — try again.")
                    )
                }
        }
    }

    private fun sendEffect(effect: ManualInvoiceEditEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

package com.samduka.dukacred.feature.invoicecapture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samduka.dukacred.core.common.result.onFailure
import com.samduka.dukacred.core.common.result.onSuccess
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.ConfirmFinancingRequestUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.EvaluateFinancingRequestUseCase
import com.samduka.dukacred.feature.invoicecapture.domain.usecase.FinancingDecision
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class InvoiceReviewViewModel(
    private val invoiceId: String,
    private val evaluateFinancingRequest: EvaluateFinancingRequestUseCase,
    private val confirmFinancingRequest: ConfirmFinancingRequestUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<InvoiceReviewState>(InvoiceReviewState.Evaluating)
    val state = _state.asStateFlow()

    private val _effect = Channel<InvoiceReviewEffect>(Channel.BUFFERED)
    val effect: Flow<InvoiceReviewEffect> = _effect.receiveAsFlow()

    init { evaluate(isRefresh = false) }

    fun onIntent(intent: InvoiceReviewIntent) {
        when (intent) {
            InvoiceReviewIntent.Retry -> evaluate(isRefresh = false)
            InvoiceReviewIntent.RefreshAfterEdit -> evaluate(isRefresh = true)
            InvoiceReviewIntent.NavigateBack -> sendEffect(InvoiceReviewEffect.NavigateBack)
            InvoiceReviewIntent.EditDetails -> handleEditDetails()
            InvoiceReviewIntent.ConfirmFullLoan -> confirmFullLoan()
            InvoiceReviewIntent.ConfirmAdjustedLoan -> confirmAdjustedLoan()
            is InvoiceReviewIntent.ToggleItemSelection -> toggleItem(intent.itemId)
        }
    }

    private fun evaluate(isRefresh: Boolean) {
        viewModelScope.launch {
            _state.value = if (isRefresh) {
                _state.value.withRefreshing(true) ?: InvoiceReviewState.Evaluating
            } else {
                InvoiceReviewState.Evaluating
            }

            evaluateFinancingRequest(invoiceId)
                .onSuccess { decision -> _state.value = decision.toReviewState() }
                .onFailure { error ->
                    val message = error.message ?: "We couldn't evaluate this invoice. Please try again."
                    if (isRefresh) {
                        // Re-check failed — keep the last-known-good card on
                        // screen rather than blowing it away over a transient
                        // network/backend hiccup. Surface the failure as a
                        // toast instead of an Error state.
                        _state.value = _state.value.withRefreshing(false) ?: _state.value
                        sendEffect(InvoiceReviewEffect.ShowToast(message))
                    } else {
                        _state.value = InvoiceReviewState.Error(message)
                    }
                }
        }
    }

    private fun InvoiceReviewState.withRefreshing(value: Boolean): InvoiceReviewState? = when (this) {
        is InvoiceReviewState.FullApproval -> copy(isRefreshing = value)
        is InvoiceReviewState.PartialAdjustment -> copy(isRefreshing = value)
        else -> null
    }

    private fun handleEditDetails() {
        currentInvoiceOrNull()?.let { sendEffect(InvoiceReviewEffect.NavigateToManualEdit(it)) }
    }

    private fun confirmFullLoan() {
        if (_state.value !is InvoiceReviewState.FullApproval) return
        viewModelScope.launch {
            confirmFinancingRequest(invoiceId, selectedItemIds = null)
                .onSuccess { loanId -> sendEffect(InvoiceReviewEffect.NavigateToFinancingSuccess(loanId)) }
                .onFailure { error ->
                    sendEffect(InvoiceReviewEffect.ShowToast(error.message ?: "Couldn't confirm loan — try again."))
                }
        }
    }

    private fun confirmAdjustedLoan() {
        val current = _state.value as? InvoiceReviewState.PartialAdjustment ?: return
        val selectedIds = current.items.filter { it.isSelected }.map { it.id }.toSet()
        if (selectedIds.isEmpty()) {
            sendEffect(InvoiceReviewEffect.ShowToast("Select at least one item to finance."))
            return
        }
        viewModelScope.launch {
            confirmFinancingRequest(invoiceId, selectedIds)
                .onSuccess { loanId -> sendEffect(InvoiceReviewEffect.NavigateToFinancingSuccess(loanId)) }
                .onFailure { error ->
                    sendEffect(InvoiceReviewEffect.ShowToast(error.message ?: "Couldn't confirm loan — try again."))
                }
        }
    }

    private fun toggleItem(itemId: String) {
        val current = _state.value as? InvoiceReviewState.PartialAdjustment ?: return
        val updatedItems = current.items.map { item ->
            if (item.id == itemId) item.copy(isSelected = !item.isSelected) else item
        }
        _state.value = current.copy(
            items = updatedItems,
            adjustedTotal = updatedItems.filter { it.isSelected }.sumOf { it.amount },
        )
    }

    private fun currentInvoiceOrNull(): ParsedInvoice? = when (val s = _state.value) {
        is InvoiceReviewState.FullApproval -> s.invoice
        is InvoiceReviewState.PartialAdjustment -> s.invoice
        else -> null
    }

    private fun sendEffect(effect: InvoiceReviewEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun FinancingDecision.toReviewState(): InvoiceReviewState = when (this) {
        is FinancingDecision.Approved -> InvoiceReviewState.FullApproval(
            invoice = invoice, termDays = termDays, interestRate = interestRate,
        )
        is FinancingDecision.PartiallyApproved -> InvoiceReviewState.PartialAdjustment(
            invoice = invoice,
            maxCoveredLimit = maxCoveredLimit,
            items = items,
            adjustedTotal = items.filter { it.isSelected }.sumOf { it.amount },
        )
        is FinancingDecision.Rejected -> InvoiceReviewState.Error(reason)
    }
}
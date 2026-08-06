package com.samduka.dukacred.feature.invoicecapture.presentation

import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceValidation
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice

sealed interface InvoiceProcessingState {
    data object Loading : InvoiceProcessingState
    data object ExtractingAI : InvoiceProcessingState
    data class Success(
        val invoice: ParsedInvoice,
        val validation: InvoiceValidation,
        val isSaving: Boolean = false,
    ) : InvoiceProcessingState
    data class Error(val throwable: Throwable) : InvoiceProcessingState
    data object ReScanRequested : InvoiceProcessingState
}
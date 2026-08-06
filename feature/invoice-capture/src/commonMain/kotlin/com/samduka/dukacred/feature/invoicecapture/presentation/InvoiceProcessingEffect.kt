package com.samduka.dukacred.feature.invoicecapture.presentation

sealed interface InvoiceProcessingEffect {
    data object NavigateToCapture : InvoiceProcessingEffect
    data object NavigateToDashboard : InvoiceProcessingEffect
    data class ShowError(val message: String) : InvoiceProcessingEffect
}
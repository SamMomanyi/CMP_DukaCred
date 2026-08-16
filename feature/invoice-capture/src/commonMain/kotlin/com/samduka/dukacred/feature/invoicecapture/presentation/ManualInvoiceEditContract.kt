// presentation/ManualInvoiceEditContract.kt
package com.samduka.dukacred.feature.invoicecapture.presentation

import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice

data class ManualInvoiceEditState(
    val invoice: ParsedInvoice? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ManualInvoiceEditIntent {
    data class MerchantNameChanged(val value: String) : ManualInvoiceEditIntent
    data class InvoiceDateChanged(val value: String) : ManualInvoiceEditIntent
    data class InvoiceNumberChanged(val value: String) : ManualInvoiceEditIntent
    data class TotalAmountChanged(val value: Double) : ManualInvoiceEditIntent

    data class LineItemChanged(val lineItem: InvoiceLineItem) : ManualInvoiceEditIntent
    data class LineItemRemoved(val localId: String) : ManualInvoiceEditIntent
    data object LineItemAdded : ManualInvoiceEditIntent

    data object SaveClicked : ManualInvoiceEditIntent
    data object DiscardClicked : ManualInvoiceEditIntent
    data object Retry : ManualInvoiceEditIntent
}

sealed interface ManualInvoiceEditEffect {
    data object NavigateBack : ManualInvoiceEditEffect
    data class ShowError(val message: String) : ManualInvoiceEditEffect
}
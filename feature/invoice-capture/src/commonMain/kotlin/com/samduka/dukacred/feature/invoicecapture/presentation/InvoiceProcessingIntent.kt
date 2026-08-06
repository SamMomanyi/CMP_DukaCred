package com.samduka.dukacred.feature.invoicecapture.presentation

import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem

sealed interface InvoiceProcessingIntent {
    data object Retry : InvoiceProcessingIntent

    data class MerchantNameChanged(val value: String) : InvoiceProcessingIntent
    data class InvoiceDateChanged(val value: String) : InvoiceProcessingIntent
    data class InvoiceNumberChanged(val value: String) : InvoiceProcessingIntent
    data class TotalAmountChanged(val value: Double) : InvoiceProcessingIntent
    data class TaxAmountChanged(val value: Double?) : InvoiceProcessingIntent

    data class LineItemChanged(val lineItem: InvoiceLineItem) : InvoiceProcessingIntent
    data class LineItemRemoved(val localId: String) : InvoiceProcessingIntent
    data object LineItemAdded : InvoiceProcessingIntent

    data object ConfirmAndSaveClicked : InvoiceProcessingIntent
    data object RetakeClicked : InvoiceProcessingIntent
}
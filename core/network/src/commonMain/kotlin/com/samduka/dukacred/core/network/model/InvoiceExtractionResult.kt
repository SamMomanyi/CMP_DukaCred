package com.samduka.dukacred.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvoiceExtractionResult(
    @SerialName("merchant_name") val merchantName: String? = null,
    @SerialName("total_amount") val totalAmount: Double? = null,
    @SerialName("vat_amount") val vatAmount: Double? = null,
    @SerialName("currency") val currency: String? = "KES",
    @SerialName("invoice_date") val invoiceDate: String? = null,
    @SerialName("invoice_number") val invoiceNumber: String? = null,
    @SerialName("line_items") val lineItems: List<LineItem> = emptyList(), // FIX: was `List` (no type arg)
)

@Serializable
data class LineItem(
    @SerialName("description") val description: String,
    @SerialName("quantity") val quantity: Double? = 1.0,
    @SerialName("unit_price") val unitPrice: Double? = null,
    @SerialName("total_price") val totalPrice: Double? = null,
)
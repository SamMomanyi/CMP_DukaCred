package com.samduka.dukacred.core.domain.model

data class InvoiceLineItem(
    val description: String,
    val quantity: Double,
    val unitPrice: Money,
    val totalPrice: Money,
)

data class Invoice(
    val id: InvoiceId,
    val supplierId: SupplierId,
    val supplierName: String,
    val invoiceNumber: String,
    val invoiceDate: String,
    val totalAmount: Money,
    val tillNumber: String?,
    val lineItems: List<InvoiceLineItem> = emptyList(),
    val imageUri: String,
    val extractionConfidence: ConfidenceLevel = ConfidenceLevel.HIGH,
    val extractionFlags: List<String> = emptyList(),
)
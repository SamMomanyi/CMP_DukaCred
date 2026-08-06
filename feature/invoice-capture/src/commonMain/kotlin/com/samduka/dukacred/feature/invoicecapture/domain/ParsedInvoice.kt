package com.samduka.dukacred.feature.invoicecapture.domain

import kotlin.random.Random

/**
 * The structured, merchant-reviewable data extracted from an invoice/receipt
 * image. `id` is null until the invoice has actually been persisted to
 * Supabase — a freshly-extracted, not-yet-confirmed invoice always has
 * id = null.
 */
data class ParsedInvoice(
    val id: String? = null,
    val merchantName: String,
    val invoiceDate: String,
    val invoiceNumber: String? = null,
    val totalAmount: Double,
    val currency: String = "KES",
    val taxAmount: Double? = null,
    val lineItems: List<InvoiceLineItem> = emptyList(),
    val paymentMethod: PaymentMethod = PaymentMethod.UNKNOWN,
    val mpesaTransactionRef: String? = null,
    val tillNumber: String? = null,
    val etrSerialNumber: String? = null,
    val isVerified: Boolean = false,
)

data class InvoiceLineItem(
    // Stable per-row identity for Compose list editing (add/remove/reorder) —
    // NOT a database id. Plain kotlin.random rather than java.util.UUID,
    // which isn't available on the iOS/Native targets this app ships to.
    val localId: String = generateLocalId(),
    val description: String,
    val quantity: Double = 1.0,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = quantity * unitPrice,
)

enum class PaymentMethod { CASH, MPESA, CARD, BANK_TRANSFER, UNKNOWN }

fun generateLocalId(): String = Random.nextLong().toString()
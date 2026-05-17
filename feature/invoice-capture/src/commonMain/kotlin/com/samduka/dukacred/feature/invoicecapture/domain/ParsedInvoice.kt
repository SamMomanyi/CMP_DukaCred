package com.samduka.dukacred.feature.invoicecapture.domain.model

/**
 * The structured data we expect Claude 3.5 Sonnet to extract
 * from the raw merchant invoice image.
 */
data class ParsedInvoice(
    val merchantName: String,
    val date: String,
    val totalAmount: Double,
    val taxAmount: Double? = null,
    val isVerified: Boolean = false
)
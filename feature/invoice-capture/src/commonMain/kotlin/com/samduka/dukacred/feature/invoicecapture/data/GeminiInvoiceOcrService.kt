package com.samduka.dukacred.feature.invoicecapture.data

import com.samduka.dukacred.core.network.api.GeminiInvoiceApi
import com.samduka.dukacred.core.network.model.InvoiceExtractionResult
import com.samduka.dukacred.core.network.model.LineItem as LineItemDto
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceOcrService
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class GeminiInvoiceOcrService(
    private val geminiApi: GeminiInvoiceApi,
) : InvoiceOcrService {

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun extractInvoiceData(imageBytes: ByteArray): Result<ParsedInvoice> = runCatching {
        // kotlin.io.encoding.Base64 rather than java.util.Base64 / android.util.Base64
        // — both of those are JVM/Android-only and this module also targets iOS.
        val base64Image = Base64.encode(imageBytes)
        geminiApi.extractInvoiceDetails(base64Image).toDomain()
    }
}

private fun InvoiceExtractionResult.toDomain(): ParsedInvoice = ParsedInvoice(
    merchantName = merchantName?.trim()?.takeUnless { it.isBlank() } ?: "Unknown merchant",
    invoiceDate = invoiceDate?.trim()?.takeUnless { it.isBlank() } ?: "",
    invoiceNumber = invoiceNumber?.trim()?.takeUnless { it.isBlank() },
    totalAmount = totalAmount ?: 0.0,
    currency = currency?.trim()?.takeUnless { it.isBlank() } ?: "KES",
    taxAmount = vatAmount,
    lineItems = lineItems.map { it.toDomain() },
    isVerified = false, // stays false until the merchant confirms on the review screen
)

private fun LineItemDto.toDomain(): InvoiceLineItem {
    val qty = quantity ?: 1.0
    val unit = unitPrice ?: 0.0
    return InvoiceLineItem(
        description = description.ifBlank { "Item" },
        quantity = qty,
        unitPrice = unit,
        totalPrice = totalPrice ?: (qty * unit),
    )
}
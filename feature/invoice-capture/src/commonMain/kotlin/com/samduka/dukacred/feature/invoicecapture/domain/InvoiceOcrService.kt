package com.samduka.dukacred.feature.invoicecapture.domain

/**
 * Contract for extracting structured data from an invoice image.
 * Implementation talks to Gemini 2.5 Flash via GeminiInvoiceApi (:core:network).
 */
interface InvoiceOcrService {
    // FIX: was `Result<String>` — now returns the mapped domain model
    // directly, so nothing above this layer needs to know Gemini/JSON exists.
    suspend fun extractInvoiceData(imageBytes: ByteArray): Result<ParsedInvoice>
}
package com.samduka.dukacred.feature.invoicecapture.domain

/**
 * Contract for extracting structured data from an invoice image.
 * Implementation will use AWS Bedrock (Claude 3.5 Sonnet) via S3 pre-signed URLs.
 */
interface InvoiceOcrService {
    suspend fun extractInvoiceData(imageBytes: ByteArray): Result<String>
    // Note: We'll change String to a real Data Class (Merchant, Total, Date) later!
}
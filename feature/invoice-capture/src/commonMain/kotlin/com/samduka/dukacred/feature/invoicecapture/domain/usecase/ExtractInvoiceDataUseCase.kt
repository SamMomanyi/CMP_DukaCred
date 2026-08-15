package com.samduka.dukacred.feature.invoicecapture.domain.usecase

import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceDraft
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceOcrService

/** Sends a JPEG to Gemini and returns its merchant-reviewable invoice draft. */
class ExtractInvoiceDataUseCase(
    private val invoiceOcrService: InvoiceOcrService,
) {
    suspend operator fun invoke(compressedImage: ByteArray): Result<InvoiceDraft> {
        if (compressedImage.isEmpty()) return Result.failure(IllegalArgumentException("Compressed image is empty"))
        return runCatching { invoiceOcrService.extractInvoiceData(compressedImage).getOrThrow() }
    }
}

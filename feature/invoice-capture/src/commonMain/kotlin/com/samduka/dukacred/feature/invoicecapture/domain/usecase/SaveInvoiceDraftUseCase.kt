package com.samduka.dukacred.feature.invoicecapture.domain.usecase

import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceDraft
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository

/** Uploads the receipt JPEG and persists the merchant-confirmed draft to Supabase. */
class SaveInvoiceDraftUseCase(
    private val invoiceRepository: InvoiceRepository,
) {
    suspend operator fun invoke(draft: InvoiceDraft, compressedImage: ByteArray): Result<InvoiceDraft> {
        if (compressedImage.isEmpty()) return Result.failure(IllegalArgumentException("Compressed image is empty"))
        return invoiceRepository.confirmAndSaveInvoice(draft, compressedImage)
    }
}

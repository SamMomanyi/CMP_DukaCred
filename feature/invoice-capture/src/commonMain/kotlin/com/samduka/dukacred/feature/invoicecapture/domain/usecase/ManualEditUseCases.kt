
package com.samduka.dukacred.feature.invoicecapture.domain.usecase

import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository

class GetInvoiceByIdUseCase(private val invoiceRepository: InvoiceRepository) {
    suspend operator fun invoke(invoiceId: String): Result<ParsedInvoice> =
        invoiceRepository.getInvoiceById(invoiceId)
}

/** saveInvoice() is already an upsert per its own doc comment — thin, honest alias. */
class UpdateInvoiceDraftUseCase(private val invoiceRepository: InvoiceRepository) {
    suspend operator fun invoke(invoice: ParsedInvoice): Result<ParsedInvoice> =
        invoiceRepository.saveInvoice(invoice)
}
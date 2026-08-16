// domain/usecase/ManualEditUseCases.kt
package com.samduka.dukacred.feature.invoicecapture.domain.usecase


import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.first

/**
 * PROPOSED — InvoiceRepository doesn't expose these two methods in what
 * I've seen. Wrapping them in use cases now so the presentation layer is
 * ready; Backend AI adds the repository methods + implementations per §16.
 */
class GetInvoiceByIdUseCase(private val invoiceRepository: InvoiceRepository) {
    suspend operator fun invoke(invoiceId: String): Result<ParsedInvoice> = runCatching {
        invoiceRepository.getInvoiceHistory().first()
            .firstOrNull { it.id == invoiceId }
            ?: throw NoSuchElementException("Invoice $invoiceId not found")
    }
}

/** saveInvoice() is already an upsert per its own doc comment — thin, honest alias. */
class UpdateInvoiceDraftUseCase(private val invoiceRepository: InvoiceRepository) {
    suspend operator fun invoke(invoice: ParsedInvoice): Result<ParsedInvoice> =
        invoiceRepository.saveInvoice(invoice)}
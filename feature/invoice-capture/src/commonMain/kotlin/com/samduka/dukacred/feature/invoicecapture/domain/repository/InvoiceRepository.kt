package com.samduka.dukacred.feature.invoicecapture.domain.repository

import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Single source of truth for invoice data.
 *
 * Split into two steps rather than one `processAndSaveInvoice` call because
 * the actual UX has a review step in between: extract → merchant edits the
 * form → THEN save. Saving whatever Gemini produced without letting the
 * merchant correct it first defeats the point of the verification screen.
 */
interface InvoiceRepository {

    /** AI extraction only — no persistence. Feeds the editable review form. */
    suspend fun extractInvoice(imageBytes: ByteArray): Result<ParsedInvoice>

    /** Persists a merchant-verified invoice row and its line items. */
    suspend fun saveInvoice(invoice: ParsedInvoice): Result<ParsedInvoice>

    /** Stream of saved invoices for history/dashboard surfaces. */
    fun getInvoiceHistory(): Flow<List<ParsedInvoice>>

    /**
     * Single-invoice fetch by id. Default impl piggybacks on
     * [getInvoiceHistory] until a dedicated query exists — same tradeoff as
     * [getRecentInvoices] below. SupabaseInvoiceRepository can override this
     * with a real point query later without breaking any existing call site.
     */
    suspend fun getInvoiceById(invoiceId: String): Result<ParsedInvoice> = runCatching {
        getInvoiceHistory().first().firstOrNull { it.id == invoiceId }
            ?: throw NoSuchElementException("Invoice $invoiceId not found")
    }

    /** Uploads a private receipt image and returns its storage object path. */
    suspend fun uploadInvoiceImage(bytes: ByteArray): Result<String>

    suspend fun confirmAndSaveInvoice(
        invoice: ParsedInvoice,
        rawImageBytes: ByteArray,
    ): Result<ParsedInvoice> = runCatching {
        val imagePath = uploadInvoiceImage(rawImageBytes).getOrThrow()
        saveInvoice(invoice.copy(imagePath = imagePath)).getOrThrow()
    }

    fun getRecentInvoices(): Flow<List<ParsedInvoice>> = getInvoiceHistory()
}

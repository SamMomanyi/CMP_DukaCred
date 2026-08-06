package com.samduka.dukacred.feature.invoicecapture.domain.repository

import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import kotlinx.coroutines.flow.Flow

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

    /** Persists the merchant-verified invoice + uploads the raw receipt image. */
    suspend fun confirmAndSaveInvoice(
        invoice: ParsedInvoice,
        rawImageBytes: ByteArray,
    ): Result<ParsedInvoice>

    /** Stream of all saved invoices for the dashboard/history UI. */
    fun getRecentInvoices(): Flow<List<ParsedInvoice>>
}
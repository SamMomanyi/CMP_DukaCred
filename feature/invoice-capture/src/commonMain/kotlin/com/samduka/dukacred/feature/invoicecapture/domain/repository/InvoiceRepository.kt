package com.samduka.dukacred.feature.invoicecapture.domain.repository

import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for invoice data.
 * Coordinates between the OCR service and local local database.
 */
interface InvoiceRepository {

    // Sends the image to the AI, gets the data, and saves it locally
    suspend fun processAndSaveInvoice(imageBytes: ByteArray): Result<ParsedInvoice>

    // Gets a stream of all saved invoices for the UI to display
    fun getRecentInvoices(): Flow<List<ParsedInvoice>>
}
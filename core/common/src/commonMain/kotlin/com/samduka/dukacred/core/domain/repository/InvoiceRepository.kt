package com.samduka.dukacred.core.domain.repository

import com.samduka.dukacred.core.common.error.ExtractionError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.Invoice

data class InvoiceDocumentInput(
    val imageUri: String,
    val imageBytes: ByteArray? = null,
)

interface InvoiceExtractionRepository {
    suspend fun extractInvoice(document: InvoiceDocumentInput): AppResult<Invoice, ExtractionError>
}
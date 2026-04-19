package com.samduka.dukacred.core.domain.repository

import com.samduka.dukacred.core.common.error.ExtractionError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.Invoice

data class InvoiceDocumentInput(
    val imageUri: String,
    val imageBytes: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as InvoiceDocumentInput

        if (imageUri != other.imageUri) return false
        if (!imageBytes.contentEquals(other.imageBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageUri.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        return result
    }
}

interface InvoiceExtractionRepository {
    suspend fun extractInvoice(document: InvoiceDocumentInput): AppResult<Invoice, ExtractionError>
}
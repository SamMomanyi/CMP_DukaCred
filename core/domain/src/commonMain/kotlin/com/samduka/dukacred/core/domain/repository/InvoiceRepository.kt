package com.samduka.dukacred.core.domain.repository

import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.error.StorageError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.Invoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    suspend fun saveInvoice(invoice: Invoice): AppResult<Invoice, AppError>

    fun getInvoiceHistory(): Flow<List<Invoice>>

    suspend fun uploadInvoiceImage(bytes: ByteArray): AppResult<String, StorageError>
}

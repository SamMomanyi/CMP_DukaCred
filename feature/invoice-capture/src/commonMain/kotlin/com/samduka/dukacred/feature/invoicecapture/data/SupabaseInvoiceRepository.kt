package com.samduka.dukacred.feature.invoicecapture.data

import com.samduka.dukacred.feature.invoicecapture.data.dto.DukaLookupDto
import com.samduka.dukacred.feature.invoicecapture.data.dto.InvoiceDto
import com.samduka.dukacred.feature.invoicecapture.data.dto.InvoiceItemDto
import com.samduka.dukacred.feature.invoicecapture.data.dto.toDomain
import com.samduka.dukacred.feature.invoicecapture.data.dto.toDto
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceOcrService
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

private const val INVOICES_TABLE = "invoices"
private const val INVOICE_ITEMS_TABLE = "invoice_items"
private const val DUKAS_TABLE = "dukas"
private const val INVOICES_BUCKET = "invoice-scans"

class SupabaseInvoiceRepository(
    private val ocrService: InvoiceOcrService,
    private val supabaseClient: SupabaseClient,
) : InvoiceRepository {

    override suspend fun extractInvoice(imageBytes: ByteArray): Result<ParsedInvoice> =
        ocrService.extractInvoiceData(imageBytes)

    override suspend fun saveInvoice(invoice: ParsedInvoice): Result<ParsedInvoice> = runCatching {
        val ownerId = currentOwnerId()
        val dukaId = currentDukaId(ownerId)
        val savedInvoice = supabaseClient.postgrest.from(INVOICES_TABLE)
            .insert(invoice.toDto(ownerId = ownerId, dukaId = dukaId)) { select() }
            .decodeSingle<InvoiceDto>()

        val invoiceId = requireNotNull(savedInvoice.id) { "Supabase did not return an invoice id" }
        val savedItems = if (invoice.lineItems.isEmpty()) {
            emptyList()
        } else {
            supabaseClient.postgrest.from(INVOICE_ITEMS_TABLE)
                .insert(invoice.lineItems.mapIndexed { index, item -> item.toDto(invoiceId, index) }) { select() }
                .decodeList<InvoiceItemDto>()
        }

        savedInvoice.toDomain(savedItems)
    }

    override fun getInvoiceHistory(): Flow<List<ParsedInvoice>> = flow {
        val ownerId = currentOwnerId()
        val invoices = supabaseClient.postgrest.from(INVOICES_TABLE)
            .select {
                filter { eq("owner_id", ownerId) }
                order("invoice_date", Order.DESCENDING)
            }
            .decodeList<InvoiceDto>()

        emit(
            invoices.map { invoice ->
                val invoiceId = invoice.id
                val items = if (invoiceId == null) {
                    emptyList()
                } else {
                    supabaseClient.postgrest.from(INVOICE_ITEMS_TABLE)
                        .select {
                            filter { eq("invoice_id", invoiceId) }
                            order("position", Order.ASCENDING)
                        }
                        .decodeList<InvoiceItemDto>()
                }
                invoice.toDomain(items)
            }
        )
    }

    override suspend fun uploadInvoiceImage(bytes: ByteArray): Result<String> = runCatching {
        require(bytes.isNotEmpty()) { "Invoice image is empty" }
        val ownerId = currentOwnerId()
        val imagePath = "$ownerId/invoice_${Random.nextInt(0, Int.MAX_VALUE)}_${Random.nextInt(0, Int.MAX_VALUE)}.jpg"

        supabaseClient.storage.from(INVOICES_BUCKET)
            .upload(imagePath, bytes) { upsert = false }

        imagePath
    }

    override suspend fun confirmAndSaveInvoice(
        invoice: ParsedInvoice,
        rawImageBytes: ByteArray,
    ): Result<ParsedInvoice> = runCatching {
        val imagePath = uploadInvoiceImage(rawImageBytes).getOrThrow()
        saveInvoice(invoice.copy(imagePath = imagePath)).getOrThrow()
    }

    private fun currentOwnerId(): String =
        supabaseClient.auth.currentSessionOrNull()?.user?.id
            ?: error("No active Supabase session")

    private suspend fun currentDukaId(ownerId: String): String =
        supabaseClient.postgrest.from(DUKAS_TABLE)
            .select {
                filter { eq("owner_id", ownerId) }
                limit(1)
            }
            .decodeSingle<DukaLookupDto>()
            .id
}

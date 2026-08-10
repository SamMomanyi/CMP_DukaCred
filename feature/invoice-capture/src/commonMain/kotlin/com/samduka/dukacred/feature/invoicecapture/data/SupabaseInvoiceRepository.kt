package com.samduka.dukacred.feature.invoicecapture.data

import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceOcrService
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

private const val INVOICES_TABLE = "invoices"
private const val INVOICES_BUCKET = "invoice-images"

/**
 * Assumes a Supabase table shaped roughly like this — adjust to match
 * whatever you actually provision:
 *
 * create table invoices (
 *   id uuid primary key default gen_random_uuid(),
 *   merchant_name text not null,
 *   invoice_date text not null,
 *   invoice_number text,
 *   total_amount numeric not null,
 *   currency text not null default 'KES',
 *   tax_amount numeric,
 *   line_items jsonb not null default '[]'::jsonb,
 *   image_path text,
 *   is_verified boolean not null default true,
 *   created_at timestamptz not null default now()
 * );
 *
 * Plus a public Storage bucket named "invoice-images" for the raw JPEGs.
 */
class SupabaseInvoiceRepository(
    private val ocrService: InvoiceOcrService,
    private val supabaseClient: SupabaseClient,
) : InvoiceRepository {

    override suspend fun extractInvoice(imageBytes: ByteArray): Result<ParsedInvoice> =
        ocrService.extractInvoiceData(imageBytes)

    override suspend fun confirmAndSaveInvoice(
        invoice: ParsedInvoice,
        rawImageBytes: ByteArray,
    ): Result<ParsedInvoice> = runCatching {
        val imagePath = "invoice_${Random.nextInt(0, Int.MAX_VALUE)}_${Random.nextInt(0, Int.MAX_VALUE)}.jpg"

        supabaseClient.storage.from(INVOICES_BUCKET)
            .upload(imagePath, rawImageBytes) { upsert = false }

        val saved = supabaseClient.postgrest.from(INVOICES_TABLE)
            .insert(invoice.toRow(imagePath = imagePath)) { select() }
            .decodeSingle<InvoiceRow>()

        saved.toDomain()
    }

    override fun getRecentInvoices(): Flow<List<ParsedInvoice>> = flow {
        val rows = supabaseClient.postgrest.from(INVOICES_TABLE)
            .select()
            .decodeList<InvoiceRow>()
        emit(rows.map { it.toDomain() })
    }
}

@Serializable
private data class InvoiceRow(
    val id: String? = null,
    @SerialName("merchant_name") val merchantName: String,
    @SerialName("invoice_date") val invoiceDate: String,
    @SerialName("invoice_number") val invoiceNumber: String? = null,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("currency") val currency: String,
    @SerialName("tax_amount") val taxAmount: Double? = null,
    @SerialName("line_items") val lineItems: List<InvoiceLineItemRow> = emptyList(),
    @SerialName("image_path") val imagePath: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = true,
)

@Serializable
private data class InvoiceLineItemRow(
    val description: String,
    val quantity: Double,
    @SerialName("unit_price") val unitPrice: Double,
    @SerialName("total_price") val totalPrice: Double,
)

private fun ParsedInvoice.toRow(imagePath: String?): InvoiceRow = InvoiceRow(
    id = id?.takeIf { it.isNotBlank() },
    merchantName = merchantName,
    invoiceDate = invoiceDate,
    invoiceNumber = invoiceNumber,
    totalAmount = totalAmount,
    currency = currency,
    taxAmount = taxAmount,
    lineItems = lineItems.map { InvoiceLineItemRow(it.description, it.quantity, it.unitPrice, it.totalPrice) },
    imagePath = imagePath,
    isVerified = true,
)

private fun InvoiceRow.toDomain(): ParsedInvoice = ParsedInvoice(
    id = id,
    merchantName = merchantName,
    invoiceDate = invoiceDate,
    invoiceNumber = invoiceNumber,
    totalAmount = totalAmount,
    currency = currency,
    taxAmount = taxAmount,
    lineItems = lineItems.map {
        InvoiceLineItem(description = it.description, quantity = it.quantity, unitPrice = it.unitPrice, totalPrice = it.totalPrice)
    },
    isVerified = isVerified,
)
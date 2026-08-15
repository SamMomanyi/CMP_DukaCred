package com.samduka.dukacred.feature.invoicecapture.data.dto

import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvoiceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("duka_id") val dukaId: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("supplier_name") val supplierName: String,
    @SerialName("supplier_till_number") val supplierTillNumber: String? = null,
    @SerialName("invoice_number") val invoiceNumber: String? = null,
    @SerialName("invoice_date") val invoiceDate: String,
    @SerialName("total_amount_cents") val totalAmountCents: Long,
    @SerialName("tax_amount_cents") val taxAmountCents: Long? = null,
    @SerialName("currency") val currency: String = "KES",
    @SerialName("status") val status: String = "verified",
    @SerialName("image_path") val imagePath: String? = null,
    @SerialName("extraction_confidence") val extractionConfidence: Double = 1.0,
    @SerialName("extraction_flags") val extractionFlags: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class InvoiceItemDto(
    @SerialName("id") val id: String? = null,
    @SerialName("invoice_id") val invoiceId: String,
    @SerialName("description") val description: String,
    @SerialName("quantity") val quantity: Double = 1.0,
    @SerialName("unit_price_cents") val unitPriceCents: Long,
    @SerialName("total_price_cents") val totalPriceCents: Long,
    @SerialName("position") val position: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
)

fun ParsedInvoice.toDto(
    ownerId: String,
    dukaId: String,
): InvoiceDto = InvoiceDto(
    id = id?.takeIf { it.isNotBlank() },
    dukaId = dukaId,
    ownerId = ownerId,
    supplierName = merchantName,
    supplierTillNumber = supplierTillNumber,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    totalAmountCents = totalAmount.toCents(),
    taxAmountCents = taxAmount?.toCents(),
    currency = currency,
    imagePath = imagePath,
)

fun InvoiceDto.toDomain(lineItems: List<InvoiceItemDto> = emptyList()): ParsedInvoice = ParsedInvoice(
    id = id,
    merchantName = supplierName,
    invoiceDate = invoiceDate,
    invoiceNumber = invoiceNumber,
    totalAmount = totalAmountCents.toMajorUnits(),
    currency = currency,
    taxAmount = taxAmountCents?.toMajorUnits(),
    lineItems = lineItems.map { it.toDomain() },
    imagePath = imagePath,
    isVerified = status == "verified",
    supplierTillNumber = supplierTillNumber,
)

fun InvoiceLineItem.toDto(
    invoiceId: String,
    position: Int,
): InvoiceItemDto = InvoiceItemDto(
    invoiceId = invoiceId,
    description = description,
    quantity = quantity,
    unitPriceCents = unitPrice.toCents(),
    totalPriceCents = totalPrice.toCents(),
    position = position,
)

fun InvoiceItemDto.toDomain(): InvoiceLineItem = InvoiceLineItem(
    description = description,
    quantity = quantity,
    unitPrice = unitPriceCents.toMajorUnits(),
    totalPrice = totalPriceCents.toMajorUnits(),
)

private fun Double.toCents(): Long = (this * 100).toLong()

private fun Long.toMajorUnits(): Double = this / 100.0

@Serializable
data class DukaLookupDto(
    @SerialName("id") val id: String,
)

package com.samduka.dukacred.core.network.dto

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

@Serializable
data class CashflowAggregateDto(
    @SerialName("id") val id: String? = null,
    @SerialName("duka_id") val dukaId: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("aggregate_date") val aggregateDate: String,
    @SerialName("gross_sales_cents") val grossSalesCents: Long,
    @SerialName("invoice_spend_cents") val invoiceSpendCents: Long,
    @SerialName("net_cashflow_cents") val netCashflowCents: Long,
    @SerialName("transaction_count") val transactionCount: Int,
    @SerialName("source") val source: String = "synthetic_seed",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

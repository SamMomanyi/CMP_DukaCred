package com.samduka.dukacred.feature.financing.data.dto

import com.samduka.dukacred.feature.financing.domain.model.CashflowAggregate
import com.samduka.dukacred.feature.financing.domain.model.CashflowSource
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

fun CashflowAggregate.toDto(): CashflowAggregateDto = CashflowAggregateDto(
    id = id,
    dukaId = dukaId,
    ownerId = ownerId,
    aggregateDate = aggregateDate.toString(),
    grossSalesCents = grossSalesCents,
    invoiceSpendCents = invoiceSpendCents,
    netCashflowCents = netCashflowCents,
    transactionCount = transactionCount,
    source = source.toStorageValue(),
)

fun CashflowAggregateDto.toDomain(): CashflowAggregate = CashflowAggregate(
    id = id,
    dukaId = dukaId,
    ownerId = ownerId,
    aggregateDate = LocalDate.parse(aggregateDate),
    grossSalesCents = grossSalesCents,
    invoiceSpendCents = invoiceSpendCents,
    netCashflowCents = netCashflowCents,
    transactionCount = transactionCount,
    source = source.toCashflowSource(),
)

private fun CashflowSource.toStorageValue(): String = when (this) {
    CashflowSource.POS -> "pos"
    CashflowSource.INVOICE -> "invoice"
    CashflowSource.SYNTHETIC_SEED -> "synthetic_seed"
}

private fun String.toCashflowSource(): CashflowSource = when (this) {
    "pos" -> CashflowSource.POS
    "invoice" -> CashflowSource.INVOICE
    else -> CashflowSource.SYNTHETIC_SEED
}

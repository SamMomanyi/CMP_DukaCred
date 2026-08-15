package com.samduka.dukacred.core.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CashflowAggregate(
    val id: String? = null,
    val dukaId: String,
    val ownerId: String,
    val aggregateDate: LocalDate,
    val grossSalesCents: Long,
    val invoiceSpendCents: Long,
    val netCashflowCents: Long,
    val transactionCount: Int,
    val source: CashflowSource = CashflowSource.SYNTHETIC_SEED,
)

@Serializable
enum class CashflowSource {
    POS,
    INVOICE,
    SYNTHETIC_SEED,
}

@Serializable
data class CashflowTimeRange(
    val startDate: LocalDate,
    val endDate: LocalDate,
)

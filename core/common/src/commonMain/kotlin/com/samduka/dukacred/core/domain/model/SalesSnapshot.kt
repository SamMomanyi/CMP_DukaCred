package com.samduka.dukacred.core.domain.model

data class SalesSnapshot(
    val merchantId: MerchantId,
    val averageDailySalesKes: Double,
    val sevenDayTotalKes: Double,
    val consistencyScore: Double,
    val trend: SalesTrend,
)

enum class SalesTrend { GROWING, STABLE, DECLINING }
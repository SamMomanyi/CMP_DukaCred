package com.samduka.dukacred.feature.financing.domain.repository

import com.samduka.dukacred.feature.financing.domain.model.CashflowAggregate
import com.samduka.dukacred.feature.financing.domain.model.CashflowTimeRange
import kotlinx.coroutines.flow.Flow

interface CashflowRepository {
    fun getAggregatedCashflow(timeRange: CashflowTimeRange): Flow<List<CashflowAggregate>>

    suspend fun upsertCashflowAggregate(aggregate: CashflowAggregate): Result<CashflowAggregate>
}

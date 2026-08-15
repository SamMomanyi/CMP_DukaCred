package com.samduka.dukacred.core.domain.repository

import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.CashflowAggregate
import com.samduka.dukacred.core.domain.model.CashflowTimeRange
import kotlinx.coroutines.flow.Flow

interface CashflowRepository {
    fun getAggregatedCashflow(timeRange: CashflowTimeRange): Flow<List<CashflowAggregate>>

    suspend fun upsertCashflowAggregate(
        aggregate: CashflowAggregate,
    ): AppResult<CashflowAggregate, AppError>
}

package com.samduka.dukacred.feature.financing.data

import com.samduka.dukacred.feature.financing.data.dto.CashflowAggregateDto
import com.samduka.dukacred.feature.financing.data.dto.toDomain
import com.samduka.dukacred.feature.financing.data.dto.toDto
import com.samduka.dukacred.feature.financing.domain.model.CashflowAggregate
import com.samduka.dukacred.feature.financing.domain.model.CashflowTimeRange
import com.samduka.dukacred.feature.financing.domain.repository.CashflowRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val CASHFLOW_AGGREGATES_TABLE = "cashflow_aggregates"

class SupabaseCashflowRepository(
    private val supabaseClient: SupabaseClient,
) : CashflowRepository {
    override fun getAggregatedCashflow(timeRange: CashflowTimeRange): Flow<List<CashflowAggregate>> = flow {
        val ownerId = currentOwnerId()
        val rows = supabaseClient.postgrest.from(CASHFLOW_AGGREGATES_TABLE)
            .select {
                filter {
                    eq("owner_id", ownerId)
                    gte("aggregate_date", timeRange.startDate.toString())
                    lte("aggregate_date", timeRange.endDate.toString())
                }
                order("aggregate_date", Order.ASCENDING)
            }
            .decodeList<CashflowAggregateDto>()

        emit(rows.map { it.toDomain() })
    }

    override suspend fun upsertCashflowAggregate(
        aggregate: CashflowAggregate,
    ): Result<CashflowAggregate> = runCatching {
        val saved = supabaseClient.postgrest.from(CASHFLOW_AGGREGATES_TABLE)
            .upsert(aggregate.toDto()) {
                onConflict = "duka_id,aggregate_date"
                select()
            }
            .decodeSingle<CashflowAggregateDto>()

        saved.toDomain()
    }

    private fun currentOwnerId(): String =
        supabaseClient.auth.currentSessionOrNull()?.user?.id
            ?: error("No active Supabase session")
}

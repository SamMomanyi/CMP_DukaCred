package com.samduka.dukacred.core.domain.repository

import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.FinancingContract
import com.samduka.dukacred.core.domain.model.FinancingRequest
import com.samduka.dukacred.core.domain.model.Money

data class PortfolioSummary(
    val activeContractCount: Int,
    val totalOutstandingAmount: Money,
    val overdueContractCount: Int,
    val totalOverdueAmount: Money,
    val pendingReviewCount: Int,
    val approvalRatePercent: Double,
)

interface PortfolioRepository {
    suspend fun getPortfolioSummary(): AppResult<PortfolioSummary, AppError>
    suspend fun getPendingRequests(): AppResult<List<FinancingRequest>, AppError>
    suspend fun getOverdueContracts(): AppResult<List<FinancingContract>, AppError>
}
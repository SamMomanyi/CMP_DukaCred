package com.samduka.dukacred.core.domain.repository

import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.error.ValidationError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.FinancingContract
import com.samduka.dukacred.core.domain.model.FinancingRequest
import com.samduka.dukacred.core.domain.model.Invoice
import com.samduka.dukacred.core.domain.model.MerchantId
import com.samduka.dukacred.core.domain.model.Money
import com.samduka.dukacred.core.domain.model.RequestId

data class FinancingDraft(
    val merchantId: MerchantId,
    val invoice: Invoice,
    val requestedAmount: Money,
)

interface FinancingRepository {
    suspend fun submitRequest(draft: FinancingDraft): AppResult<FinancingRequest, ValidationError>
    suspend fun getMerchantRequests(merchantId: MerchantId): AppResult<List<FinancingRequest>, AppError>
    suspend fun getRequestDetails(requestId: RequestId): AppResult<FinancingRequest, AppError>
    suspend fun approveRequest(requestId: RequestId): AppResult<FinancingContract, AppError>
    suspend fun rejectRequest(requestId: RequestId, reason: String): AppResult<Unit, AppError>
    suspend fun flagForManualReview(requestId: RequestId, note: String): AppResult<Unit, AppError>
}
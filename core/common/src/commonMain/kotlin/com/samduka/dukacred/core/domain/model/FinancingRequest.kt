package com.samduka.dukacred.core.domain.model

data class FinancingRequest(
    val id: RequestId,
    val merchantId: MerchantId,
    val invoice: Invoice,
    val requestedAmount: Money,
    val status: FinancingRequestStatus,
    val riskAssessment: RiskAssessment? = null,
    val offer: FinancingOffer? = null,
    val submittedAt: Long? = null,
    val reviewedAt: Long? = null,
    val rejectionReason: String? = null,
)

data class RiskAssessment(
    val requestId: RequestId,
    val riskBand: RiskBand,
    val maxEligibleAmount: Money,
    val recommendedDecision: FinancingRequestStatus,
    val reasons: List<String>,
    val extractionConfidence: ConfidenceLevel,
)

data class FinancingOffer(
    val requestId: RequestId,
    val offeredAmount: Money,
    val feeAmount: Money,
    val totalRepayable: Money,
    val dueDateEpochMs: Long,
    val repaymentWindowDays: Int = 7,
)
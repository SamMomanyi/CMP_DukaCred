// domain/usecase/FinancingReviewUseCases.kt
package com.samduka.dukacred.feature.invoicecapture.domain.usecase


import com.samduka.dukacred.core.common.error.RiskPolicyError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.presentation.FinancingItem

/**
 * PROPOSED — not yet implemented. Backend AI owns the real Trust Engine
 * evaluation (architecture doc §7 RiskAssessment); this is the minimum
 * shape InvoiceReviewViewModel needs, per §16 handoff protocol.
 */
interface EvaluateFinancingRequestUseCase {
    suspend operator fun invoke(invoiceId: String): AppResult<FinancingDecision, RiskPolicyError>
}

interface ConfirmFinancingRequestUseCase {
    suspend operator fun invoke(
        invoiceId: String,
        selectedItemIds: Set<String>? = null, // null = full approval
    ): AppResult<String, RiskPolicyError> // Success = loanId
}

sealed interface FinancingDecision {
    data class Approved(
        val invoice: ParsedInvoice,
        val termDays: Int,
        val interestRate: Double,
    ) : FinancingDecision

    data class PartiallyApproved(
        val invoice: ParsedInvoice,
        val maxCoveredLimit: Double,
        val items: List<FinancingItem>,
    ) : FinancingDecision

    data class Rejected(val reason: String) : FinancingDecision
}
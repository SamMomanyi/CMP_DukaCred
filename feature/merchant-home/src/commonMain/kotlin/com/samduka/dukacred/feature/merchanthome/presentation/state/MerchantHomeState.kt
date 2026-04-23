package com.samduka.dukacred.feature.merchanthome.presentation.state

import com.samduka.dukacred.core.common.mvi.UiState
import com.samduka.dukacred.core.domain.model.ContractStatus
import com.samduka.dukacred.core.domain.model.FinancingRequestStatus
import com.samduka.dukacred.core.domain.model.Money

data class ObligationUiModel(
    val id: String,
    val invoiceReference: String,
    val principalAmount: Money,
    val outstandingAmount: Money?,
    val nextPaymentAmount: Money?,
    val nextPaymentDueDate: String?,
    val contractStatus: ContractStatus?,
    val requestStatus: FinancingRequestStatus?,
) {
    val statusLabel: String
        get() = when {
            contractStatus != null -> contractStatus.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
            requestStatus != null  -> requestStatus.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
            else                   -> "Unknown"
        }

    val isUrgent: Boolean
        get() = contractStatus == ContractStatus.OVERDUE || contractStatus == ContractStatus.DEFAULTED

    val isPositive: Boolean
        get() = contractStatus == ContractStatus.ACTIVE || requestStatus == FinancingRequestStatus.APPROVED

    val isPending: Boolean
        get() = !isUrgent && !isPositive
}

data class MerchantHomeState(
    val merchantName: String = "",
    val availableCredit: Money = Money.ZERO,
    val totalCreditLimit: Money = Money.ZERO,
    val obligations: List<ObligationUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState {
    val creditUsageFraction: Float
        get() {
            val total = totalCreditLimit.amountCents.toFloat()
            if (total == 0f) return 0f
            val used = (total - availableCredit.amountCents.toFloat()).coerceAtLeast(0f)
            return (used / total).coerceIn(0f, 1f)
        }

    companion object {
        val fakeState = MerchantHomeState(
            merchantName = "Sam's Duka",
            availableCredit = Money(amountCents = 185_000_00L),
            totalCreditLimit = Money(amountCents = 250_000_00L),
            obligations = listOf(
                ObligationUiModel(
                    id = "obl-001",
                    invoiceReference = "INV-2025-0042",
                    principalAmount = Money(amountCents = 45_000_00L),
                    outstandingAmount = Money(amountCents = 30_000_00L),
                    nextPaymentAmount = Money(amountCents = 7_500_00L),
                    nextPaymentDueDate = "2025-08-15",
                    contractStatus = ContractStatus.ACTIVE,
                    requestStatus = null,
                ),
                ObligationUiModel(
                    id = "obl-002",
                    invoiceReference = "INV-2025-0031",
                    principalAmount = Money(amountCents = 20_000_00L),
                    outstandingAmount = Money(amountCents = 20_000_00L),
                    nextPaymentAmount = Money(amountCents = 20_000_00L),
                    nextPaymentDueDate = "2025-07-28",
                    contractStatus = ContractStatus.OVERDUE,
                    requestStatus = null,
                ),
                ObligationUiModel(
                    id = "obl-003",
                    invoiceReference = "INV-2025-0057",
                    principalAmount = Money(amountCents = 60_000_00L),
                    outstandingAmount = null,
                    nextPaymentAmount = null,
                    nextPaymentDueDate = null,
                    contractStatus = null,
                    requestStatus = FinancingRequestStatus.UNDER_REVIEW,
                ),
            ),
        )
    }
}
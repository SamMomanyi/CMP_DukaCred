
package com.samduka.dukacred.feature.invoicecapture.presentation

import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice

data class FinancingItem(
    val id: String,
    val name: String,
    val amount: Double,
    val tag: String? = null,
    val warningNote: String? = null,
    val isSelected: Boolean = true,
)

sealed interface InvoiceReviewState {
    data object Evaluating : InvoiceReviewState

    data class FullApproval(
        val invoice: ParsedInvoice,
        val termDays: Int = 14,
        val interestRate: Double = 0.02,
    ) : InvoiceReviewState

    data class PartialAdjustment(
        val invoice: ParsedInvoice,
        val maxCoveredLimit: Double,
        val items: List<FinancingItem>,
        val adjustedTotal: Double,
    ) : InvoiceReviewState

    data class Error(val message: String) : InvoiceReviewState
}

sealed interface InvoiceReviewIntent {
    data class ToggleItemSelection(val itemId: String) : InvoiceReviewIntent
    data object ConfirmFullLoan : InvoiceReviewIntent
    data object ConfirmAdjustedLoan : InvoiceReviewIntent
    data object EditDetails : InvoiceReviewIntent
    data object NavigateBack : InvoiceReviewIntent
    data object Retry : InvoiceReviewIntent
}

sealed interface InvoiceReviewEffect {
    data class NavigateToFinancingSuccess(val loanId: String) : InvoiceReviewEffect
    data class NavigateToManualEdit(val invoice: ParsedInvoice) : InvoiceReviewEffect
    data object NavigateBack : InvoiceReviewEffect
    data class ShowToast(val message: String) : InvoiceReviewEffect
}
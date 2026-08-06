package com.samduka.dukacred.feature.invoicecapture.domain

import kotlin.math.abs

/** Result of reconciling a merchant's stated total against their line items. */
sealed interface InvoiceValidation {
    data object NotApplicable : InvoiceValidation
    data object Matches : InvoiceValidation
    data class Mismatch(
        val statedTotal: Double,
        val lineItemsSum: Double,
    ) : InvoiceValidation {
        val difference: Double get() = lineItemsSum - statedTotal
    }
}

object InvoiceValidator {
    // OCR'd currency figures are frequently off by a shilling or two from
    // rounding in the source document itself — anything inside this band
    // reads as "matches" rather than flagging a false discrepancy on every scan.
    private const val TOLERANCE = 0.5

    fun validate(statedTotal: Double, lineItems: List<InvoiceLineItem>): InvoiceValidation {
        if (lineItems.isEmpty()) return InvoiceValidation.NotApplicable
        val sum = lineItems.sumOf { it.totalPrice }
        return if (abs(sum - statedTotal) <= TOLERANCE) {
            InvoiceValidation.Matches
        } else {
            InvoiceValidation.Mismatch(statedTotal = statedTotal, lineItemsSum = sum)
        }
    }
}
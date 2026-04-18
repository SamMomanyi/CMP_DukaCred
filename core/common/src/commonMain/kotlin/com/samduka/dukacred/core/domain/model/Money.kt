package com.samduka.dukacred.core.domain.model

data class Money(
    val amountCents: Long,
    val currency: String = "KES",
) {
    val displayAmount: Double get() = amountCents / 100.0

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch" }
        return copy(amountCents = amountCents + other.amountCents)
    }

    operator fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Currency mismatch" }
        return amountCents.compareTo(other.amountCents)
    }

    companion object {
        fun fromKes(amount: Double) = Money(amountCents = (amount * 100).toLong())
        val ZERO = Money(amountCents = 0L)
    }
}
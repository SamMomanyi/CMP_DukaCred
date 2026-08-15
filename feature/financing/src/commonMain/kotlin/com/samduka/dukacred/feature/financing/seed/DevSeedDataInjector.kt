package com.samduka.dukacred.feature.financing.seed

import com.samduka.dukacred.feature.financing.domain.model.CashflowAggregate
import com.samduka.dukacred.feature.financing.domain.model.CashflowSource
import com.samduka.dukacred.feature.financing.domain.repository.CashflowRepository
import com.samduka.dukacred.feature.invoicecapture.domain.InvoiceLineItem
import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.domain.repository.InvoiceRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class DevSeedDataInjector(
    private val invoiceRepository: InvoiceRepository,
    private val cashflowRepository: CashflowRepository,
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    suspend fun inject(
        ownerId: String,
        dukaId: String,
    ): Result<DevSeedSummary> = runCatching {
        val today = clock.now().toLocalDateTime(timeZone).date

        syntheticCashflow(ownerId = ownerId, dukaId = dukaId, today = today)
            .forEach { aggregate -> cashflowRepository.upsertCashflowAggregate(aggregate).getOrThrow() }

        syntheticInvoices(today)
            .forEach { invoice -> invoiceRepository.saveInvoice(invoice).getOrThrow() }

        DevSeedSummary(
            cashflowDaysInjected = CASHFLOW_DAYS,
            invoicesInjected = INVOICE_COUNT,
        )
    }

    private fun syntheticCashflow(
        ownerId: String,
        dukaId: String,
        today: LocalDate,
    ): List<CashflowAggregate> =
        ((CASHFLOW_DAYS - 1) downTo 0).map { daysAgo ->
            val date = today.minus(DatePeriod(days = daysAgo))
            val weekendLift = if (date.dayOfWeek.ordinal >= 5) 1_800_00L else 0L
            val baseSales = 8_500_00L + (((CASHFLOW_DAYS - 1) - daysAgo) * 145_00L) + weekendLift
            val invoiceSpend = if (daysAgo % 6 == 0) 3_200_00L + (daysAgo * 35_00L) else 850_00L

            CashflowAggregate(
                dukaId = dukaId,
                ownerId = ownerId,
                aggregateDate = date,
                grossSalesCents = baseSales,
                invoiceSpendCents = invoiceSpend,
                netCashflowCents = baseSales - invoiceSpend,
                transactionCount = 36 + (((CASHFLOW_DAYS - 1) - daysAgo) % 14),
                source = CashflowSource.SYNTHETIC_SEED,
            )
        }

    private fun syntheticInvoices(today: LocalDate): List<ParsedInvoice> {
        val suppliers = listOf(
            SeedSupplier("Nairobi Wholesale Traders", "884412"),
            SeedSupplier("Kariobangi Grains Depot", "771209"),
            SeedSupplier("Mombasa Soap Distributors", "662810"),
            SeedSupplier("Nakuru Dairy Supplies", "554901"),
            SeedSupplier("Thika Beverage House", "448230"),
        )

        return suppliers.mapIndexed { index, supplier ->
            val invoiceDate = today.minus(DatePeriod(days = 5 + (index * 4)))
            val items = seedItemsFor(index)

            ParsedInvoice(
                id = "INV-SEED-${invoiceDate}-$index",
                merchantName = supplier.name,
                invoiceDate = invoiceDate.toString(),
                invoiceNumber = "SEED-${invoiceDate.toString().replace("-", "")}-$index",
                totalAmount = items.sumOf { it.totalPrice },
                currency = "KES",
                taxAmount = null,
                lineItems = items,
                imagePath = "invoice-scans/seed/${invoiceDate}-$index.jpg",
                isVerified = true,
                supplierTillNumber = supplier.tillNumber,
            )
        }
    }

    private fun seedItemsFor(seed: Int): List<InvoiceLineItem> {
        val catalog = listOf(
            SeedItem("Maize flour bale", 4.0, 1_420.0),
            SeedItem("Cooking oil carton", 3.0, 2_850.0),
            SeedItem("Bar soap carton", 2.0, 1_950.0),
            SeedItem("Long life milk carton", 5.0, 1_120.0),
            SeedItem("Soda crate", 6.0, 980.0),
            SeedItem("Sugar 24kg bale", 2.0, 3_250.0),
            SeedItem("Tea leaves carton", 2.0, 2_100.0),
        )

        return catalog
            .drop(seed)
            .take(3)
            .map { item ->
                InvoiceLineItem(
                    description = item.description,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    totalPrice = item.quantity * item.unitPrice,
                )
            }
    }

    private companion object {
        const val CASHFLOW_DAYS = 30
        const val INVOICE_COUNT = 5
    }
}

data class DevSeedSummary(
    val cashflowDaysInjected: Int,
    val invoicesInjected: Int,
)

private data class SeedSupplier(
    val name: String,
    val tillNumber: String,
)

private data class SeedItem(
    val description: String,
    val quantity: Double,
    val unitPrice: Double,
)

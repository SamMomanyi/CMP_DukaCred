package com.samduka.dukacred.core.domain.seed

import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.CashflowAggregate
import com.samduka.dukacred.core.domain.model.CashflowSource
import com.samduka.dukacred.core.domain.model.ConfidenceLevel
import com.samduka.dukacred.core.domain.model.Invoice
import com.samduka.dukacred.core.domain.model.InvoiceId
import com.samduka.dukacred.core.domain.model.InvoiceLineItem
import com.samduka.dukacred.core.domain.model.Money
import com.samduka.dukacred.core.domain.model.SupplierId
import com.samduka.dukacred.core.domain.repository.CashflowRepository
import com.samduka.dukacred.core.domain.repository.InvoiceRepository
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
    ): AppResult<DevSeedSummary, AppError> {
        val today = clock.now().toLocalDateTime(timeZone).date
        var cashflowRows = 0
        var invoiceRows = 0

        syntheticCashflow(ownerId = ownerId, dukaId = dukaId, today = today)
            .forEach { aggregate ->
                when (val result = cashflowRepository.upsertCashflowAggregate(aggregate)) {
                    is AppResult.Failure -> return result
                    is AppResult.Success -> cashflowRows += 1
                }
            }

        syntheticInvoices(today)
            .forEach { invoice ->
                when (val result = invoiceRepository.saveInvoice(invoice)) {
                    is AppResult.Failure -> return result
                    is AppResult.Success -> invoiceRows += 1
                }
            }

        return AppResult.Success(
            DevSeedSummary(
                cashflowDaysInjected = cashflowRows,
                invoicesInjected = invoiceRows,
            )
        )
    }

    private fun syntheticCashflow(
        ownerId: String,
        dukaId: String,
        today: LocalDate,
    ): List<CashflowAggregate> =
        (29 downTo 0).map { daysAgo ->
            val date = today.minus(DatePeriod(days = daysAgo))
            val weekdayLift = if (date.dayOfWeek.isoDayNumber >= 6) 1_800_00L else 0L
            val baseSales = 8_500_00L + ((29 - daysAgo) * 145_00L) + weekdayLift
            val invoiceSpend = if (daysAgo % 6 == 0) 3_200_00L + (daysAgo * 35_00L) else 850_00L

            CashflowAggregate(
                dukaId = dukaId,
                ownerId = ownerId,
                aggregateDate = date,
                grossSalesCents = baseSales,
                invoiceSpendCents = invoiceSpend,
                netCashflowCents = baseSales - invoiceSpend,
                transactionCount = 36 + ((29 - daysAgo) % 14),
                source = CashflowSource.SYNTHETIC_SEED,
            )
        }

    private fun syntheticInvoices(today: LocalDate): List<Invoice> {
        val suppliers = listOf(
            SeedSupplier("SUP-SEED-NAIROBI-WHOLESALE", "Nairobi Wholesale Traders", "884412"),
            SeedSupplier("SUP-SEED-KARIOBANGI-GRAINS", "Kariobangi Grains Depot", "771209"),
            SeedSupplier("SUP-SEED-MOMBASA-SOAPS", "Mombasa Soap Distributors", "662810"),
            SeedSupplier("SUP-SEED-NAKURU-DAIRY", "Nakuru Dairy Supplies", "554901"),
            SeedSupplier("SUP-SEED-THIKA-BEVERAGES", "Thika Beverage House", "448230"),
        )

        return suppliers.mapIndexed { index, supplier ->
            val invoiceDate = today.minus(DatePeriod(days = 5 + (index * 4)))
            val items = seedItemsFor(index)
            val total = items.fold(Money.ZERO) { runningTotal, item -> runningTotal + item.totalPrice }

            Invoice(
                id = InvoiceId("INV-SEED-${invoiceDate}-$index"),
                supplierId = SupplierId(supplier.id),
                supplierName = supplier.name,
                invoiceNumber = "SEED-${invoiceDate.toString().replace("-", "")}-$index",
                invoiceDate = invoiceDate.toString(),
                totalAmount = total,
                tillNumber = supplier.tillNumber,
                lineItems = items,
                imageUri = "invoice-scans/seed/${invoiceDate}-$index.jpg",
                extractionConfidence = ConfidenceLevel.HIGH,
                extractionFlags = listOf("synthetic_seed"),
            )
        }
    }

    private fun seedItemsFor(seed: Int): List<InvoiceLineItem> {
        val catalog = listOf(
            SeedItem("Maize flour bale", 4.0, 1_420_00L),
            SeedItem("Cooking oil carton", 3.0, 2_850_00L),
            SeedItem("Bar soap carton", 2.0, 1_950_00L),
            SeedItem("Long life milk carton", 5.0, 1_120_00L),
            SeedItem("Soda crate", 6.0, 980_00L),
            SeedItem("Sugar 24kg bale", 2.0, 3_250_00L),
            SeedItem("Tea leaves carton", 2.0, 2_100_00L),
        )

        return catalog
            .drop(seed)
            .take(3)
            .map { item ->
                val unitPrice = Money(amountCents = item.unitPriceCents)
                InvoiceLineItem(
                    description = item.description,
                    quantity = item.quantity,
                    unitPrice = unitPrice,
                    totalPrice = Money(amountCents = (item.quantity * item.unitPriceCents).toLong()),
                )
            }
    }
}

data class DevSeedSummary(
    val cashflowDaysInjected: Int,
    val invoicesInjected: Int,
)

private data class SeedSupplier(
    val id: String,
    val name: String,
    val tillNumber: String,
)

private data class SeedItem(
    val description: String,
    val quantity: Double,
    val unitPriceCents: Long,
)

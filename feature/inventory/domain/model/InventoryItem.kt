package com.samduka.dukacred.feature.inventory.domain.model

/**
 * Represents a single physical product sitting on the shelves of the Duka.
 * This is the core model that the Database and UI will both use.
 */
data class InventoryItem(
    val id: String,
    val name: String,
    val quantityInStock: Int,
    val buyingPrice: Double,
    val sellingPrice: Double,
    val category: String = "General",
    val lowStockAlertThreshold: Int = 5 // Warn the merchant when stock drops below this
) {
    // A little free business logic for the UI to use later!
    val expectedProfitMargin: Double
        get() = if (buyingPrice > 0) ((sellingPrice - buyingPrice) / buyingPrice) * 100 else 0.0
}
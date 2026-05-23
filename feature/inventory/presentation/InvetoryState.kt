package com.samduka.dukacred.feature.inventory.presentation

import com.samduka.dukacred.feature.inventory.domain.model.InventoryItem

/**
 * Represents everything the merchant sees on the Inventory Dashboard.
 */
data class InventoryState(
    val isLoading: Boolean = true,
    val allItems: List<InventoryItem> = emptyList(),
    val lowStockItems: List<InventoryItem> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)
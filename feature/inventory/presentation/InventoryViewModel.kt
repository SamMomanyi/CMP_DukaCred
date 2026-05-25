package com.samduka.dukacred.feature.inventory.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The brain of the Inventory Dashboard.
 * Listens to merchant actions (Intents) and updates the UI (State).
 */
class InventoryViewModel : ViewModel() {

    // 1. The Single Source of Truth for the UI
    private val _state = MutableStateFlow(InventoryState())sudo
    val state = _state.asStateFlow()

    // 2. The switchboard for user actions
    fun onIntent(intent: InventoryIntent) {
        when (intent) {
            is InventoryIntent.SearchQueryChanged -> {
                // Update the search bar text immediately
                _state.value = _state.value.copy(searchQuery = intent.query)
            }
            is InventoryIntent.RecordSale -> {
                // TODO: Call the repository to deduct stock
            }
            is InventoryIntent.AddStockClicked -> {
                // TODO: Trigger navigation to the invoice scanner
            }
            is InventoryIntent.RefreshInventory -> {
                // TODO: Re-fetch the latest stock from the SQLDelight database
            }
        }
    }
}
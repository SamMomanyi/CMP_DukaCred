package com.samduka.dukacred.feature.merchanthome.presentation.action

sealed interface MerchantHomeAction {
    data object RefreshData : MerchantHomeAction
    data object CaptureInvoiceClicked : MerchantHomeAction
    data object PayClicked : MerchantHomeAction
    data object HistoryClicked : MerchantHomeAction
    data object NotificationsClicked : MerchantHomeAction
    data class ObligationClicked(val id: String) : MerchantHomeAction
}
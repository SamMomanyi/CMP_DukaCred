package com.samduka.dukacred.feature.merchanthome.presentation.effect

import com.samduka.dukacred.core.common.mvi.UiEffect

sealed interface MerchantHomeEffect : UiEffect {
    data object NavigateToInvoiceCapture : MerchantHomeEffect
    data object NavigateToPayment : MerchantHomeEffect
    data object NavigateToHistory : MerchantHomeEffect
    data object NavigateToNotifications : MerchantHomeEffect
    data class NavigateToObligationDetails(val id: String) : MerchantHomeEffect
}
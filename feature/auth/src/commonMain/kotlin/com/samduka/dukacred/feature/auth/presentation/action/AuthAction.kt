package com.samduka.dukacred.feature.auth.presentation.action

import com.samduka.dukacred.core.common.mvi.UiAction

sealed interface RolePickerAction : UiAction {
    data object SelectMerchant : RolePickerAction
    data object SelectAdmin    : RolePickerAction
}

sealed interface MerchantSignInAction : UiAction {
    data class PhoneNumberChanged(val value: String) : MerchantSignInAction
    data class PinChanged(val value: String)         : MerchantSignInAction
    data object SignInClicked                         : MerchantSignInAction
    data object BackClicked                           : MerchantSignInAction
}

sealed interface AdminSignInAction : UiAction {
    data class EmailChanged(val value: String)       : AdminSignInAction
    data class PasswordChanged(val value: String)    : AdminSignInAction
    data object TogglePasswordVisibility             : AdminSignInAction
    data object SignInClicked                        : AdminSignInAction
    data object BackClicked                          : AdminSignInAction
}
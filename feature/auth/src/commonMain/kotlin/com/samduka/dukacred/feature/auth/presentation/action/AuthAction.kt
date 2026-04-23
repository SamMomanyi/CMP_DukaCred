package com.samduka.dukacred.feature.auth.presentation.action

import com.samduka.dukacred.core.common.mvi.UiAction

sealed interface RolePickerAction : UiAction {
    data object SelectMerchant : RolePickerAction
    data object SelectAdmin    : RolePickerAction
}

// ... existing code
sealed interface MerchantSignInAction {
    data class EmailChanged(val value: String) : MerchantSignInAction
    data class PasswordChanged(val value: String) : MerchantSignInAction
    data object TogglePasswordVisibility : MerchantSignInAction
    data object SignInClicked : MerchantSignInAction
    data object SignUpClicked : MerchantSignInAction
    data object BackClicked : MerchantSignInAction
}

sealed interface AdminSignInAction : UiAction {
    data class EmailChanged(val value: String)       : AdminSignInAction
    data class PasswordChanged(val value: String)    : AdminSignInAction
    data object TogglePasswordVisibility             : AdminSignInAction
    data object SignInClicked                        : AdminSignInAction
    data object BackClicked                          : AdminSignInAction

    data object SignUpClicked : AdminSignInAction
}



package com.samduka.dukacred.feature.auth.presentation.effect

import com.samduka.dukacred.core.common.mvi.UiEffect

sealed interface RolePickerEffect : UiEffect {
    data object NavigateToMerchantSignIn : RolePickerEffect
    data object NavigateToAdminSignIn    : RolePickerEffect
    data object NavigateToMerchantHome   : RolePickerEffect
    data object NavigateToAdminQueue     : RolePickerEffect
}

sealed interface MerchantSignInEffect : UiEffect {
    data object NavigateToMerchantHome : MerchantSignInEffect
    data object NavigateBack           : MerchantSignInEffect
    data object NavigateToSignUp       : MerchantSignInEffect
}

sealed interface AdminSignInEffect : UiEffect {
    data object NavigateToAdminQueue : AdminSignInEffect
    data object NavigateBack         : AdminSignInEffect
    data object NavigateToSignUp     : AdminSignInEffect
}

sealed interface SignUpEffect : UiEffect {
    data object NavigateToMerchantHome : SignUpEffect
    data object NavigateToAdminQueue   : SignUpEffect
    data object NavigateBack           : SignUpEffect
}
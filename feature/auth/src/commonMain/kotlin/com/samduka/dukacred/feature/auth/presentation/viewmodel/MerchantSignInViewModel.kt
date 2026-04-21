package com.samduka.dukacred.feature.auth.presentation.viewmodel

import com.samduka.dukacred.core.common.mvi.BaseViewModel
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.auth.domain.usecase.SignInMerchantUseCase
import com.samduka.dukacred.feature.auth.presentation.action.MerchantSignInAction
import com.samduka.dukacred.feature.auth.presentation.effect.MerchantSignInEffect
import com.samduka.dukacred.feature.auth.presentation.state.MerchantSignInState

class MerchantSignInViewModel(
    private val signInMerchant: SignInMerchantUseCase,
) : BaseViewModel<MerchantSignInState, MerchantSignInAction, MerchantSignInEffect>(
    initialState = MerchantSignInState(),
) {
    override fun onAction(action: MerchantSignInAction) {
        when (action) {
            is MerchantSignInAction.PhoneNumberChanged ->
                updateState { it.copy(phoneNumber = action.value, phoneError = null) }

            is MerchantSignInAction.PinChanged ->
                updateState { it.copy(pin = action.value, pinError = null) }

            MerchantSignInAction.SignInClicked -> signIn()

            MerchantSignInAction.BackClicked ->
                sendEffect(MerchantSignInEffect.NavigateBack)
        }
    }

    private fun signIn() {
        val current = state.value
        // Clear previous errors
        updateState { it.copy(
            isLoading    = true,
            generalError = null,
            phoneError   = null,
            pinError     = null,
        )}

        launch {
            val result = signInMerchant(
                phoneNumber = current.phoneNumber.trim(),
                pin         = current.pin.trim(),
            )
            when (result) {
                is AppResult.Success -> {
                    updateState { it.copy(isLoading = false) }
                    sendEffect(MerchantSignInEffect.NavigateToMerchantHome)
                }
                is AppResult.Failure -> {
                    updateState { it.copy(
                        isLoading    = false,
                        generalError = result.error.message,
                    )}
                }
            }
        }
    }
}
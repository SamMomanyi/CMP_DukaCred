package com.samduka.dukacred.feature.auth.presentation.viewmodel

import com.samduka.dukacred.core.common.mvi.BaseViewModel
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.auth.domain.usecase.SignInAdminUseCase
import com.samduka.dukacred.feature.auth.presentation.action.AdminSignInAction
import com.samduka.dukacred.feature.auth.presentation.effect.AdminSignInEffect
import com.samduka.dukacred.feature.auth.presentation.state.AdminSignInState

class AdminSignInViewModel(
    private val signInAdmin: SignInAdminUseCase,
) : BaseViewModel<AdminSignInState, AdminSignInAction, AdminSignInEffect>(
    initialState = AdminSignInState(),
) {
    override fun onAction(action: AdminSignInAction) {
        when (action) {
            is AdminSignInAction.EmailChanged ->
                updateState { it.copy(email = action.value, emailError = null) }

            is AdminSignInAction.PasswordChanged ->
                updateState { it.copy(password = action.value, passwordError = null) }

            AdminSignInAction.TogglePasswordVisibility ->
                updateState { it.copy(isPasswordVisible = !state.value.isPasswordVisible) }

            AdminSignInAction.SignInClicked -> signIn()

            AdminSignInAction.BackClicked ->
                sendEffect(AdminSignInEffect.NavigateBack)
        }
    }

    private fun signIn() {
        val current = state.value
        updateState { it.copy(
            isLoading     = true,
            generalError  = null,
            emailError    = null,
            passwordError = null,
        )}

        launch {
            val result = signInAdmin(
                email    = current.email.trim(),
                password = current.password,
            )
            when (result) {
                is AppResult.Success -> {
                    updateState { it.copy(isLoading = false) }
                    sendEffect(AdminSignInEffect.NavigateToAdminQueue)
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
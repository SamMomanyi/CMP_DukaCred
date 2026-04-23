package com.samduka.dukacred.feature.auth.presentation.viewmodel

import com.samduka.dukacred.core.common.mvi.BaseViewModel
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.UserRole
import com.samduka.dukacred.feature.auth.domain.usecase.SignUpUseCase
import com.samduka.dukacred.feature.auth.presentation.action.SignUpAction
import com.samduka.dukacred.feature.auth.presentation.effect.SignUpEffect
import com.samduka.dukacred.feature.auth.presentation.state.SignUpState

class SignUpViewModel(
    private val signUp: SignUpUseCase,
) : BaseViewModel<SignUpState, SignUpAction, SignUpEffect>(SignUpState()) {

    override fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.EmailChanged           -> updateState { it.copy(email = action.value, emailError = null) }
            is SignUpAction.PasswordChanged        -> updateState { it.copy(password = action.value, passwordError = null) }
            is SignUpAction.ConfirmPasswordChanged -> updateState { it.copy(confirmPassword = action.value) }
            is SignUpAction.RoleSelected           -> updateState { it.copy(selectedRole = action.role) }
            SignUpAction.TogglePasswordVisibility  -> updateState { it.copy(isPasswordVisible = !state.value.isPasswordVisible) }
            SignUpAction.BackClicked               -> sendEffect(SignUpEffect.NavigateBack)
            SignUpAction.SignUpClicked             -> register()
        }
    }

    private fun register() {
        val s = state.value
        if (s.password != s.confirmPassword) {
            updateState { it.copy(passwordError = "Passwords do not match") }
            return
        }
        updateState { it.copy(isLoading = true, generalError = null, emailError = null, passwordError = null) }
        launch {
            when (val result = signUp(s.email.trim(), s.password, s.selectedRole)) {
                is AppResult.Success -> {
                    updateState { it.copy(isLoading = false) }
                    when (s.selectedRole) {
                        UserRole.MERCHANT -> sendEffect(SignUpEffect.NavigateToMerchantHome)
                        UserRole.ADMIN    -> sendEffect(SignUpEffect.NavigateToAdminQueue)
                    }
                }
                is AppResult.Failure -> updateState { it.copy(isLoading = false, generalError = result.error.message) }
            }
        }
    }
}
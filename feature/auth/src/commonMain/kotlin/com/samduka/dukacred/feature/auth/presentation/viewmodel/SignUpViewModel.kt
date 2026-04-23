package com.samduka.dukacred.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.UserRole
import com.samduka.dukacred.feature.auth.domain.usecase.SignUpUseCase
import com.samduka.dukacred.feature.auth.presentation.action.SignUpAction
import com.samduka.dukacred.feature.auth.presentation.effect.SignUpEffect
import com.samduka.dukacred.feature.auth.presentation.state.SignUpState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUp: SignUpUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SignUpEffect>()
    val effects = _effects.asSharedFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.EmailChanged           -> _state.update { it.copy(email = action.value, emailError = null) }
            is SignUpAction.PasswordChanged        -> _state.update { it.copy(password = action.value, passwordError = null) }
            is SignUpAction.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = action.value) }
            is SignUpAction.RoleSelected           -> _state.update { it.copy(selectedRole = action.role) }
            SignUpAction.TogglePasswordVisibility  -> _state.update { it.copy(isPasswordVisible = !state.value.isPasswordVisible) }
            SignUpAction.BackClicked               -> sendEffect(SignUpEffect.NavigateBack)
            SignUpAction.SignUpClicked             -> register()
        }
    }

    private fun register() {
        val s = state.value
        if (s.password != s.confirmPassword) {
            _state.update { it.copy(passwordError = "Passwords do not match") }
            return
        }
        _state.update { it.copy(isLoading = true, generalError = null, emailError = null, passwordError = null) }

        viewModelScope.launch {
            when (val result = signUp(s.email.trim(), s.password, s.selectedRole)) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    when (s.selectedRole) {
                        UserRole.MERCHANT -> sendEffect(SignUpEffect.NavigateToMerchantHome)
                        UserRole.ADMIN    -> sendEffect(SignUpEffect.NavigateToAdminQueue)
                    }
                }
                is AppResult.Failure -> _state.update { it.copy(isLoading = false, generalError = result.error.message) }
            }
        }
    }

    private fun sendEffect(effect: SignUpEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }
}
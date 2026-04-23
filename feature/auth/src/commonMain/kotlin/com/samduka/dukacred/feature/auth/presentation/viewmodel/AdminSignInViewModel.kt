package com.samduka.dukacred.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.auth.domain.usecase.SignInAdminUseCase
import com.samduka.dukacred.feature.auth.presentation.action.AdminSignInAction
import com.samduka.dukacred.feature.auth.presentation.effect.AdminSignInEffect
import com.samduka.dukacred.feature.auth.presentation.state.AdminSignInState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminSignInViewModel(
    private val signInAdmin: SignInAdminUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AdminSignInState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AdminSignInEffect>()
    val effects = _effects.asSharedFlow()

    fun onAction(action: AdminSignInAction) {
        when (action) {
            is AdminSignInAction.EmailChanged ->
                _state.update { it.copy(email = action.value, emailError = null) }

            is AdminSignInAction.PasswordChanged ->
                _state.update { it.copy(password = action.value, passwordError = null) }

            AdminSignInAction.TogglePasswordVisibility ->
                _state.update { it.copy(isPasswordVisible = !state.value.isPasswordVisible) }

            AdminSignInAction.SignInClicked -> signIn()

            AdminSignInAction.BackClicked ->
                sendEffect(AdminSignInEffect.NavigateBack)

            AdminSignInAction.SignUpClicked ->
                sendEffect(AdminSignInEffect.NavigateToSignUp)
        }
    }

    private fun signIn() {
        val current = state.value
        _state.update { it.copy(
            isLoading     = true,
            generalError  = null,
            emailError    = null,
            passwordError = null,
        )}

        viewModelScope.launch {
            val result = signInAdmin(
                email    = current.email.trim(),
                password = current.password,
            )
            when (result) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(AdminSignInEffect.NavigateToAdminQueue)
                }
                is AppResult.Failure -> {
                    _state.update { it.copy(
                        isLoading    = false,
                        generalError = result.error.message,
                    )}
                }
            }
        }
    }

    private fun sendEffect(effect: AdminSignInEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }
}
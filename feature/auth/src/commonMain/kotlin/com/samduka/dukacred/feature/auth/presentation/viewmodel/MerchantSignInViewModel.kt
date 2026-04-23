package com.samduka.dukacred.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.auth.domain.usecase.SignInMerchantUseCase
import com.samduka.dukacred.feature.auth.presentation.action.MerchantSignInAction
import com.samduka.dukacred.feature.auth.presentation.effect.MerchantSignInEffect
import com.samduka.dukacred.feature.auth.presentation.state.MerchantSignInState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MerchantSignInViewModel(
    private val signInMerchant: SignInMerchantUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MerchantSignInState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<MerchantSignInEffect>()
    val effects = _effects.asSharedFlow()

    fun onAction(action: MerchantSignInAction) {
        when (action) {
            is MerchantSignInAction.EmailChanged ->
                _state.update { it.copy(email = action.value, emailError = null) }
            is MerchantSignInAction.PasswordChanged ->
                _state.update { it.copy(password = action.value, passwordError = null) }
            MerchantSignInAction.TogglePasswordVisibility ->
                _state.update { it.copy(isPasswordVisible = !state.value.isPasswordVisible) }
            MerchantSignInAction.SignInClicked -> signIn()
            MerchantSignInAction.SignUpClicked -> sendEffect(MerchantSignInEffect.NavigateToSignUp)
            MerchantSignInAction.BackClicked -> sendEffect(MerchantSignInEffect.NavigateBack)
        }
    }

    private fun signIn() {
        val current = state.value
        _state.update { it.copy(isLoading = true, generalError = null, emailError = null, passwordError = null) }

        viewModelScope.launch {

            val result = signInMerchant(email = current.email.trim(), password = current.password)
            when (result) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(MerchantSignInEffect.NavigateToMerchantHome)
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

    private fun sendEffect(effect: MerchantSignInEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }
}
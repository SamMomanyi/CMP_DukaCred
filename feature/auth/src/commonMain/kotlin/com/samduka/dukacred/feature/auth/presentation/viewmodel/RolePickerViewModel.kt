package com.samduka.dukacred.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.UserRole
import com.samduka.dukacred.feature.auth.domain.usecase.RestoreSessionUseCase
import com.samduka.dukacred.feature.auth.presentation.action.RolePickerAction
import com.samduka.dukacred.feature.auth.presentation.effect.RolePickerEffect
import com.samduka.dukacred.feature.auth.presentation.state.RolePickerState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RolePickerViewModel(
    private val restoreSession: RestoreSessionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RolePickerState(isCheckingSession = true))
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<RolePickerEffect>()
    val effects = _effects.asSharedFlow()

    init {
        // On launch check if a valid session exists
        // so the user doesn't have to sign in every time
        checkExistingSession()
    }

    fun onAction(action: RolePickerAction) {
        when (action) {
            RolePickerAction.SelectMerchant ->
                sendEffect(RolePickerEffect.NavigateToMerchantSignIn)
            RolePickerAction.SelectAdmin ->
                sendEffect(RolePickerEffect.NavigateToAdminSignIn)
        }
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            val result = restoreSession()
            _state.update { it.copy(isCheckingSession = false) }

            when (result) {
                is AppResult.Success -> {
                    val user = result.data ?: return@launch
                    // Already signed in — skip the picker entirely
                    when (user.role) {
                        UserRole.MERCHANT ->
                            sendEffect(RolePickerEffect.NavigateToMerchantHome)
                        UserRole.ADMIN ->
                            sendEffect(RolePickerEffect.NavigateToAdminQueue)
                    }
                }
                is AppResult.Failure -> {
                    // No session — stay on role picker, no error needed
                }
            }
        }
    }

    private fun sendEffect(effect: RolePickerEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }
}
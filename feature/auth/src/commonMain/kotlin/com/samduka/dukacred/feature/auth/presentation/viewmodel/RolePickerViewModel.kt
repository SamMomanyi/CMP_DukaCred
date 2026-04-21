package com.samduka.dukacred.feature.auth.presentation.viewmodel

import com.samduka.dukacred.core.common.mvi.BaseViewModel
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.UserRole
import com.samduka.dukacred.feature.auth.domain.usecase.RestoreSessionUseCase
import com.samduka.dukacred.feature.auth.presentation.action.RolePickerAction
import com.samduka.dukacred.feature.auth.presentation.effect.RolePickerEffect
import com.samduka.dukacred.feature.auth.presentation.state.RolePickerState

class RolePickerViewModel(
    private val restoreSession: RestoreSessionUseCase,
) : BaseViewModel<RolePickerState, RolePickerAction, RolePickerEffect>(
    initialState = RolePickerState(isCheckingSession = true),
) {
    init {
        // On launch check if a valid session exists
        // so the user doesn't have to sign in every time
        checkExistingSession()
    }

    override fun onAction(action: RolePickerAction) {
        when (action) {
            RolePickerAction.SelectMerchant ->
                sendEffect(RolePickerEffect.NavigateToMerchantSignIn)
            RolePickerAction.SelectAdmin ->
                sendEffect(RolePickerEffect.NavigateToAdminSignIn)
        }
    }

    private fun checkExistingSession() {
        launch {
            val result = restoreSession()
            updateState { it.copy(isCheckingSession = false) }
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
}
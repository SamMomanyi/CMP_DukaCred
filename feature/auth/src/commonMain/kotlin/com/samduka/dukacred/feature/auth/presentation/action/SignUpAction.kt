package com.samduka.dukacred.feature.auth.presentation.action

import com.samduka.dukacred.core.common.mvi.UiAction
import com.samduka.dukacred.core.domain.model.UserRole

sealed interface SignUpAction : UiAction {
    data class EmailChanged(val value: String) : SignUpAction
    data class PasswordChanged(val value: String) : SignUpAction
    data class ConfirmPasswordChanged(val value: String) : SignUpAction
    data class RoleSelected(val role: UserRole) : SignUpAction
    data object TogglePasswordVisibility : SignUpAction
    data object SignUpClicked : SignUpAction
    data object BackClicked : SignUpAction
}
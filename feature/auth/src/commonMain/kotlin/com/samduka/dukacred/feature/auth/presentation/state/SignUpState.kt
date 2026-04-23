package com.samduka.dukacred.feature.auth.presentation.state

import com.samduka.dukacred.core.common.mvi.UiState
import com.samduka.dukacred.core.domain.model.UserRole

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedRole: UserRole = UserRole.MERCHANT,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
) : UiState
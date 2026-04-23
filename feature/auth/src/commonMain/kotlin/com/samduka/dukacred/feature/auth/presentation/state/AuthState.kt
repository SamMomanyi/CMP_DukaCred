package com.samduka.dukacred.feature.auth.presentation.state

import com.samduka.dukacred.core.common.mvi.UiState
import com.samduka.dukacred.core.domain.model.UserRole

data class RolePickerState(
    val isCheckingSession: Boolean = true,
) : UiState

// ... existing code
data class MerchantSignInState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null
)

data class AdminSignInState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
) : UiState
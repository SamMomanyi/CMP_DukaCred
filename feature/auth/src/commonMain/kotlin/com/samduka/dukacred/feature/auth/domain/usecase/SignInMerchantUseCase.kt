package com.samduka.dukacred.feature.auth.domain.usecase

import com.samduka.dukacred.core.common.error.AuthError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.auth.domain.model.AuthUser
import com.samduka.dukacred.feature.auth.domain.repository.AuthRepository

class SignInMerchantUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): AppResult<AuthUser, AuthError> {
        // Validate before hitting the network
        if (email.isBlank()) {
            return AppResult.Failure(
                AuthError("Email cannot be empty")
            )
        }
        if (password.length < 6) {
            return AppResult.Failure(
                AuthError("Password must be at least 6 characters")
            )
        }
        return repository.signInAsMerchant(email, password)
    }
}
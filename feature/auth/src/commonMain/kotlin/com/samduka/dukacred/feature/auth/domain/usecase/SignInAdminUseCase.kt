package com.samduka.dukacred.feature.auth.domain.usecase

import com.samduka.dukacred.core.common.error.AuthError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.auth.domain.model.AuthUser
import com.samduka.dukacred.feature.auth.domain.repository.AuthRepository

class SignInAdminUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): AppResult<AuthUser, AuthError> {
        if (email.isBlank() || !email.contains("@")) {
            return AppResult.Failure(
                AuthError("Please enter a valid email address")
            )
        }
        if (password.length < 6) {
            return AppResult.Failure(
                AuthError("Password must be at least 6 characters")
            )
        }
        return repository.signInAsAdmin(email, password)
    }
}
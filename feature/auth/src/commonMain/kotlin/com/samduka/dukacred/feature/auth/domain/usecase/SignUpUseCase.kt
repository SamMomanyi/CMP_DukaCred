package com.samduka.dukacred.feature.auth.domain.usecase

import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.error.ValidationError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.UserRole
import com.samduka.dukacred.feature.auth.domain.model.AuthUser
import com.samduka.dukacred.feature.auth.domain.repository.AuthRepository

class SignUpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        role: UserRole,
    ): AppResult<AuthUser, AppError> {  // keep AppError here since ValidationError is also AppError
        if (email.isBlank() || !email.contains('@'))
            return AppResult.Failure(ValidationError("Invalid email"))  // AppError.ValidationError -> ValidationError
        if (password.length < 6)
            return AppResult.Failure(ValidationError("Password must be at least 6 characters"))  // same
        return repository.signUp(email, password, role)
    }
}
package com.samduka.dukacred.feature.auth.domain.usecase

import com.samduka.dukacred.core.common.error.AuthError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.feature.auth.domain.model.AuthUser
import com.samduka.dukacred.feature.auth.domain.repository.AuthRepository

class SignInMerchantUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        phoneNumber: String,
        pin: String,
    ): AppResult<AuthUser, AuthError> {
        // Validate before hitting the network
        if (phoneNumber.isBlank()) {
            return AppResult.Failure(
                AuthError("Phone number cannot be empty")
            )
        }
        if (pin.length < 4) {
            return AppResult.Failure(
                AuthError("PIN must be at least 4 digits")
            )
        }
        return repository.signInAsMerchant(phoneNumber, pin)
    }
}
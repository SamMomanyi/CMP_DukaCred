package com.samduka.dukacred.feature.auth.domain.repository

import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.error.AuthError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.UserRole
import com.samduka.dukacred.feature.auth.domain.model.AuthUser

interface AuthRepository {
    suspend fun signInAsMerchant(
        email: String,
        password: String,
    ): AppResult<AuthUser, AuthError>

    suspend fun signInAsAdmin(
        email: String,
        password: String,
    ): AppResult<AuthUser, AuthError>

    // Add after signOut():
    suspend fun signUp(email: String, password: String, role: UserRole): AppResult<AuthUser, AuthError>
    suspend fun getActiveSession(): AppResult<AuthUser?, AuthError>

    suspend fun signOut(): AppResult<Unit, AuthError>
}
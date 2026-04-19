package com.samduka.dukacred.core.domain.repository

import com.samduka.dukacred.core.common.error.AuthError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.UserSession

interface AuthRepository {
    suspend fun signInAsMerchant(phoneNumber: String, pin: String): AppResult<UserSession, AuthError>
    suspend fun signInAsAdmin(email: String, password: String): AppResult<UserSession, AuthError>
    suspend fun getActiveSession(): AppResult<UserSession?, AuthError>
    suspend fun signOut(): AppResult<Unit, AuthError>
}
package com.samduka.dukacred.feature.auth.data.repository

import com.samduka.dukacred.core.common.error.AuthError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.UserRole
import com.samduka.dukacred.feature.auth.domain.model.AuthUser
import com.samduka.dukacred.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.Phone
import kotlinx.serialization.json.jsonPrimitive

class AuthRepositoryImpl(
    private val supabase: SupabaseClient,
) : AuthRepository {

    override suspend fun signInAsMerchant(
        phoneNumber: String,
        pin: String,
    ): AppResult<AuthUser, AuthError> = try {
        supabase.auth.signInWith(Phone) {
            phone = phoneNumber
            password = pin
        }
        val session = supabase.auth.currentSessionOrNull()
            ?: return AppResult.Failure(AuthError("Sign in failed. Please try again."))

        AppResult.Success(session.toAuthUser(UserRole.MERCHANT))
    } catch (e: Exception) {
        AppResult.Failure(AuthError(e.toReadableMessage()))
    }

    override suspend fun signInAsAdmin(
        email: String,
        password: String,
    ): AppResult<AuthUser, AuthError> = try {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        val session = supabase.auth.currentSessionOrNull()
            ?: return AppResult.Failure(AuthError("Sign in failed. Please try again."))

        AppResult.Success(session.toAuthUser(UserRole.ADMIN))
    } catch (e: Exception) {
        AppResult.Failure(AuthError(e.toReadableMessage()))
    }

    override suspend fun getActiveSession(): AppResult<AuthUser?, AuthError> = try {
        // refreshCurrentSession restores the session from storage
        // and silently refreshes the token if expired
        supabase.auth.refreshCurrentSession()
        val session = supabase.auth.currentSessionOrNull()
        AppResult.Success(session?.toAuthUser())
    } catch (e: Exception) {
        // No valid session — return null, not an error
        // The app will route to sign in
        AppResult.Success(null)
    }

    override suspend fun signOut(): AppResult<Unit, AuthError> = try {
        supabase.auth.signOut()
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Failure(AuthError(e.toReadableMessage()))
    }
}

// ── Mappers ───────────────────────────────────────────────────────────

private fun io.github.jan.supabase.auth.user.UserSession.toAuthUser(
    roleOverride: UserRole? = null,
): AuthUser {
    val user = this.user
    // Role can come from Supabase user_metadata or be passed explicitly
    val role = roleOverride ?: run {
        val metaRole = user?.userMetadata
            ?.get("role")
            ?.jsonPrimitive
            ?.content
        when (metaRole) {
            "admin" -> UserRole.ADMIN
            else    -> UserRole.MERCHANT
        }
    }
    return AuthUser(
        id           = user?.id ?: "",
        phoneNumber  = user?.phone,
        email        = user?.email,
        displayName  = user?.userMetadata
            ?.get("display_name")
            ?.jsonPrimitive
            ?.content ?: "User",
        role         = role,
        accessToken  = this.accessToken,
        refreshToken = this.refreshToken,
        expiresAt    = this.expiresAt?.toEpochMilliseconds() ?: 0L,
    )
}

private fun Exception.toReadableMessage(): String = when {
    message?.contains("Invalid login credentials") == true ->
        "Incorrect phone number or PIN. Please try again."
    message?.contains("Email not confirmed") == true ->
        "Please verify your email before signing in."
    message?.contains("network") == true ->
        "No internet connection. Please check your network."
    else -> message ?: "An unexpected error occurred."
}
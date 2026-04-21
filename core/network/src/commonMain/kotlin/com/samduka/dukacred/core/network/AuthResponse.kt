package com.samduka.dukacred.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Matches the exact JSON Supabase returns on sign in / sign up
@Serializable
data class AuthResponse(
    @SerialName("access_token")  val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in")    val expiresIn: Int,
    @SerialName("token_type")    val tokenType: String,
    @SerialName("user")          val user: SupabaseUser,
)

@Serializable
data class SupabaseUser(
    @SerialName("id")         val id: String,
    @SerialName("email")      val email: String? = null,
    @SerialName("phone")      val phone: String? = null,
    @SerialName("role")       val role: String? = null,
    @SerialName("user_metadata") val userMetadata: UserMetadata? = null,
)

@Serializable
data class UserMetadata(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("role")         val role: String? = null,
)
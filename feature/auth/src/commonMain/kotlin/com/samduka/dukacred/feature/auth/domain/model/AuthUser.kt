package com.samduka.dukacred.feature.auth.domain.model

import com.samduka.dukacred.core.domain.model.UserRole

// This is auth-feature-specific
// Different from the core UserSession —
// this is what comes back from Supabase directly
data class AuthUser(
    val id: String,
    val phoneNumber: String?,
    val email: String?,
    val displayName: String,
    val role: UserRole,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)
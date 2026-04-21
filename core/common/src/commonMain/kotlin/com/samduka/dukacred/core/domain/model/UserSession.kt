package com.samduka.dukacred.core.domain.model


import kotlinx.datetime.Clock
data class UserSession(
    val userId: UserId,
    val merchantId: MerchantId? = null,
    val role: UserRole,
    val displayName: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val tokenExpiresAtEpochMs: Long,
) {
    val isMerchant: Boolean get() = role == UserRole.MERCHANT
    val isAdmin: Boolean get() = role == UserRole.ADMIN
    val isExpired: Boolean get() = Clock.System.now().toEpochMilliseconds() > tokenExpiresAtEpochMs
}
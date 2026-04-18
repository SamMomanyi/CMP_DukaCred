package com.samduka.dukacred.core.domain.model

data class AuditEvent(
    val id: String,
    val requestId: RequestId,
    val actorId: UserId,
    val actorRole: UserRole,
    val fromStatus: FinancingRequestStatus?,
    val toStatus: FinancingRequestStatus,
    val note: String? = null,
    val timestampEpochMs: Long,
)
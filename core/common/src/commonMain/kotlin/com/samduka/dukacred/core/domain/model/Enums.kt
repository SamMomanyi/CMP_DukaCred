package com.samduka.dukacred.core.domain.model

enum class UserRole { MERCHANT, ADMIN }

enum class FinancingRequestStatus {
    DRAFT,
    EXTRACTED,
    READY_FOR_SUBMISSION,
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    MANUAL_REVIEW,
    REJECTED,
}

enum class ContractStatus {
    PENDING_DISBURSEMENT,
    ACTIVE,
    OVERDUE,
    REPAID,
    DEFAULTED,
}

enum class DisbursementStatus { PENDING, SENT, FAILED }

enum class RiskBand { LOW, MEDIUM, HIGH }

enum class ConfidenceLevel { HIGH, MEDIUM, LOW }
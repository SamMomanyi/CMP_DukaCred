package com.samduka.dukacred.core.common.error

data class ExtractionError(
    override val message: String,
    val field: String? = null,
    val isLowConfidence: Boolean = false,
) : AppError

data class RiskPolicyError(
    override val message: String,
    val reasons: List<String> = emptyList(),
) : AppError

data class DisbursementError(
    override val message: String,
    val isRetryable: Boolean = false,
) : AppError

data class StorageError(
    override val message: String,
) : AppError
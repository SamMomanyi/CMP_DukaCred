package com.samduka.dukacred.core.common.error

sealed interface AppError {
    val message: String
}

data class NetworkError(
    override val message: String,
    val isRetryable: Boolean = true,
) : AppError

data class ValidationError(
    override val message: String,
    val field: String? = null,
) : AppError

data class AuthError(
    override val message: String,
) : AppError

data class ConfigurationError(
    override val message: String,
) : AppError

data class UnknownError(
    override val message: String = "Unknown error",
) : AppError

package com.samduka.dukacred.core.common.result

import com.samduka.dukacred.core.common.error.AppError

sealed interface AppResult<out T, out E : AppError> {
    data class Success<T>(val data: T) : AppResult<T, Nothing>
    data class Failure<E : AppError>(val error: E) : AppResult<Nothing, E>
}

inline fun <T, E : AppError, R> AppResult<T, E>.map(
    transform: (T) -> R,
): AppResult<R, E> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

inline fun <T, E : AppError> AppResult<T, E>.onSuccess(
    action: (T) -> Unit,
): AppResult<T, E> = also {
    if (this is AppResult.Success) action(data)
}

inline fun <T, E : AppError> AppResult<T, E>.onFailure(
    action: (E) -> Unit,
): AppResult<T, E> = also {
    if (this is AppResult.Failure) action(error)
}

fun <T, E : AppError> AppResult<T, E>.getOrNull(): T? =
    if (this is AppResult.Success) data else null

fun <T, E : AppError> AppResult<T, E>.errorOrNull(): E? =
    if (this is AppResult.Failure) error else null
package com.ebody.bip.core.domain.util

sealed interface Result<out T, out E> {
    data class Success<T>(val data: T) : Result<T, Nothing>
    data class Error<E>(val error: E) : Result<Nothing, E>
}

inline fun <T, E> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    when (this) {
        is Result.Success -> action(this.data)
        else -> Unit
    }
    return this
}

inline fun <T, E> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    when (this) {
        is Result.Error -> action(this.error)
        else -> Unit
    }
    return this
}

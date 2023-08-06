package ir.cafebazaar.bazaarpay.utils

import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel

sealed class Either<out V> {

    data class Success<V>(val value: V) : Either<V>()

    data class Failure(val error: ErrorModel) : Either<Nothing>()
}

inline fun <T> Either<T>.doOnSuccess(ifSuccess: (value: T) -> Unit): Either<T> {
    if (this is Either.Success) {
        ifSuccess(value)
    }
    return this
}

inline fun <T> Either<T>.doOnFailure(ifFailure: (value: ErrorModel) -> Unit): Either<T> {
    if (this is Either.Failure) {
        ifFailure(error)
    }
    return this
}
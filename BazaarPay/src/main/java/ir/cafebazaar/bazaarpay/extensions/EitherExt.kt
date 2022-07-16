package ir.cafebazaar.bazaarpay.extensions

import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.utils.Either

inline fun <R, T> Either<T>.fold(
    ifSuccess: (value: T) -> R,
    ifFailure: (failure: ErrorModel) -> R
): R {
    return when (this) {
        is Either.Success<T> -> ifSuccess(value)
        is Either.Failure -> ifFailure(error)
    }
}

fun <V> Either<V>.getOrNull(): V? = (this as? Either.Success)?.value

fun <V> Either<V>.getFailureOrNull(): ErrorModel? = (this as? Either.Failure)?.error

fun <V> Either<V>.isSuccessFull(): Boolean = this is Either.Success
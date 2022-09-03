package ir.cafebazaar.bazaarpay.extensions

import ir.cafebazaar.bazaarpay.utils.Either


internal suspend inline fun <T : Any> safeApiCall(
    crossinline call: suspend () -> T
): Either<T> = runCatching {
    val data = call()
    Either.Success(data)
}.getOrElse {
    Either.Failure(asNetworkException(it))
}

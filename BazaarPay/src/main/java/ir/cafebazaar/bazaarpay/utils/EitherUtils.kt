package ir.cafebazaar.bazaarpay.utils

import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel

sealed class Either<out V> {

    data class Success<V>(val value: V) : Either<V>()

    data class Failure(val error: ErrorModel) : Either<Nothing>()
}
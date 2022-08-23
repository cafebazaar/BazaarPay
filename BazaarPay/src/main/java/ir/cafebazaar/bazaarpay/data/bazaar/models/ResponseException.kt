package ir.cafebazaar.bazaarpay.data.bazaar.models

internal data class ResponseException(
    val responseProperties: ResponseProperties?
): Throwable()
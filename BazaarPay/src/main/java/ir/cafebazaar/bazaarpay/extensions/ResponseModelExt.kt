package ir.cafebazaar.bazaarpay.extensions

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse
import ir.cafebazaar.bazaarpay.data.bazaar.models.ResponseException
import ir.cafebazaar.bazaarpay.data.bazaar.models.ResponseProperties
import ir.cafebazaar.bazaarpay.data.payment.models.PaymentBaseResponse
import ir.cafebazaar.bazaarpay.utils.Either
import ir.hamidbazargan.dynamicrestclient.response.GeneralResponseModel
import ir.hamidbazargan.dynamicrestclient.response.ResponseModel

fun <E: GeneralResponseModel> ResponseModel<E>.isSuccessful() =
    this.httpCode in 200 .. 299

fun <T, R : GeneralResponseModel> ResponseModel<R>.getEitherFromResponse(
    getSuccessBody: ()->T
): Either<T> {
    return if (isSuccessful()) {
        Either.Success(
            getSuccessBody()
        )
    } else {
        when (this.data) {
            is BazaarBaseResponse -> {
                val response = this.data as BazaarBaseResponse
                Either.Failure(ResponseException(response.properties).asNetworkException())
            }
            is PaymentBaseResponse -> {
                val response = this.data as PaymentBaseResponse
                Either.Failure(
                    ResponseException(
                    ResponseProperties(
                        response.detail.orEmpty(),
                        this.httpCode
                    )
                ).asNetworkException())
            }
            else -> {
                Either.Failure(
                    ResponseException(
                        ResponseProperties(
                            this.response.message(),
                            this.httpCode
                        )
                    ).asNetworkException()
                )
            }
        }
    }
}
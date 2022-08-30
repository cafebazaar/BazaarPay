package ir.cafebazaar.bazaarpay.data.payment

import android.content.Context
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.extensions.asNetworkException
import ir.cafebazaar.bazaarpay.extensions.getEitherFromResponse
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.request.GetPaymentMethodsRequest
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.response.PaymentMethodsInfoDto
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.response.MerchantInfoDto
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.PayRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.response.PayResponse
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType
import ir.cafebazaar.bazaarpay.utils.Either
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.base.Base

internal class PaymentRemoteDataSource {

    private var increaseBalanceRedirectUrl =
        "bazaar://${
            ServiceLocator.get<Context>().packageName
        }/increase_balance"

    private val checkoutToken: String = ServiceLocator.get(ServiceLocator.CHECKOUT_TOKEN)
    private val paymentBase: Base = ServiceLocator.get(ServiceLocator.PAYMENT)

    fun getPaymentMethods(): Either<PaymentMethodsInfo> {
        try {
            paymentBase.postMethod(
                PaymentMethodsInfoDto::class.java,
                GET_PAYMENT_METHOD_ENDPOINT
            ).apply {
                addQueryParameter(PAY_ENDPOINT_LANG, ServiceLocator.get<String>(ServiceLocator.LANGUAGE))
                setPostBody(GetPaymentMethodsRequest(checkoutToken))
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .toPaymentMethodInfo()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    fun getMerchantInfo(): Either<MerchantInfo> {
        try {
            paymentBase.getMethod(
                MerchantInfoDto::class.java,
                MERCHANT_INFO_ENDPOINT
            ).apply {
                addQueryParameter(CHECKOUT_TOKEN_LABEL, checkoutToken)
                addQueryParameter(PAY_ENDPOINT_LANG, ServiceLocator.get<String>(ServiceLocator.LANGUAGE))
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .toMerchantInfo()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    fun pay(
        paymentMethod: PaymentMethodsType,
        amount: Long?
    ): Either<PayResult> {
        try {
            paymentBase.postMethod(
                PayResponse::class.java,
                PAY_ENDPOINT
            ).apply {
                addQueryParameter(PAY_ENDPOINT_LANG, ServiceLocator.get<String>(ServiceLocator.LANGUAGE))
                setPostBody(
                    PayRequest(
                        checkoutToken,
                        paymentMethod.value,
                        amount,
                        increaseBalanceRedirectUrl
                    )
                )
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .toPayResult()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    private companion object {
        const val BASE_PATH = "pardakht/badje/v1/"
        const val MERCHANT_INFO_ENDPOINT = "${BASE_PATH}merchant-info/"
        const val GET_PAYMENT_METHOD_ENDPOINT = "${BASE_PATH}get-payment-methods/"
        const val PAY_ENDPOINT = "${BASE_PATH}pay/"
        const val PAY_ENDPOINT_LANG = "lang"
        const val CHECKOUT_TOKEN_LABEL = "checkout_token"
    }
}
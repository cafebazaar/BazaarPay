package ir.cafebazaar.bazaarpay.data.payment

import android.content.Context
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.payment.api.PaymentService
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.request.GetPaymentMethodsRequest
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.PayRequest
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.withContext

internal class PaymentRemoteDataSource {

    private val checkoutToken: String = ServiceLocator.get(ServiceLocator.CHECKOUT_TOKEN)
    private val paymentService: PaymentService by lazy {
        ServiceLocator.get()
    }

    private val globalDispatchers: GlobalDispatchers by lazy {
        ServiceLocator.get()
    }

    suspend fun getPaymentMethods(): Either<PaymentMethodsInfo> {
        val language = ServiceLocator.get<String>(ServiceLocator.LANGUAGE)
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                paymentService.getPaymentMethods(
                    GetPaymentMethodsRequest(checkoutToken),
                    language
                ).toPaymentMethodInfo()
            }
        }
    }

    suspend fun getMerchantInfo(): Either<MerchantInfo> {
        val language = ServiceLocator.get<String>(ServiceLocator.LANGUAGE)
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                paymentService.getMerchantInfo(
                    checkoutToken,
                    language
                ).toMerchantInfo()
            }
        }
    }

    suspend fun pay(
        paymentMethod: PaymentMethodsType,
        amount: Long?
    ): Either<PayResult> {
        val language = ServiceLocator.get<String>(ServiceLocator.LANGUAGE)
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                paymentService.pay(
                    PayRequest(
                        checkoutToken,
                        paymentMethod.value,
                        amount,
                        increaseBalanceRedirectUrl
                    ),
                    language
                ).toPayResult()
            }
        }
    }

    private companion object {
        private val increaseBalanceRedirectUrl =
            "bazaar://${
                ServiceLocator.get<Context>().packageName
            }/increase_balance"
    }
}
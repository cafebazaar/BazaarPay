package ir.cafebazaar.bazaarpay.data.payment

import android.content.Context
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.payment.api.PaymentService
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.DynamicCreditOption
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.request.GetPaymentMethodsRequest
import ir.cafebazaar.bazaarpay.data.payment.models.increasebalance.IncreaseBalanceRequest
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.pay.BalanceResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.InitCheckoutResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PurchaseStatus
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.CommitRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.InitCheckoutRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.PayRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.TraceRequest
import ir.cafebazaar.bazaarpay.extensions.ServiceType
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.withContext
import retrofit2.Response

internal class PaymentRemoteDataSource {

    private val checkoutToken: String by lazy { ServiceLocator.get(ServiceLocator.CHECKOUT_TOKEN) }
    private val paymentService: PaymentService by lazy { ServiceLocator.get() }
    private val globalDispatchers: GlobalDispatchers by lazy { ServiceLocator.get() }

    suspend fun getPaymentMethods(): Either<PaymentMethodsInfo> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.getPaymentMethods(
                    GetPaymentMethodsRequest(
                        checkoutToken = checkoutToken,
                        accessibility = isAccessibilityEnable(),
                    ),
                    getLanguage(),
                ).toPaymentMethodInfo()
            }
        }
    }

    suspend fun getMerchantInfo(): Either<MerchantInfo> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.getMerchantInfo(checkoutToken, getLanguage()).toMerchantInfo()
            }
        }
    }

    suspend fun pay(
        paymentMethod: String,
        amount: Long?
    ): Either<PayResult> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.pay(
                    payRequest = PayRequest(
                        checkoutToken = checkoutToken,
                        method = paymentMethod,
                        amount = amount,
                        redirectUrl = increaseBalanceRedirectUrl,
                        accessibility = isAccessibilityEnable(),
                    ),
                    lang = getLanguage(),
                ).toPayResult()
            }
        }
    }

    suspend fun increaseBalance(amount: Long): Either<PayResult> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.increaseBalance(
                    IncreaseBalanceRequest(amount, increaseBalanceRedirectUrl)
                ).toPayResult()
            }
        }
    }

    suspend fun commit(
        checkoutToken: String
    ): Either<Response<Unit>> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.commit(
                    CommitRequest(checkoutToken)
                )
            }
        }
    }

    suspend fun trace(
        checkoutToken: String
    ): Either<PurchaseStatus> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.trace(
                    TraceRequest(checkoutToken)
                ).toPurchaseState()
            }
        }
    }

    suspend fun initCheckout(
        amount: Long,
        destination: String,
        serviceName: String
    ): Either<InitCheckoutResult> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.initCheckout(
                    InitCheckoutRequest(amount, destination, serviceName)
                ).toInitCheckoutResult()
            }
        }
    }

    suspend fun getBalance(): Either<BalanceResult> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.getBalance().toBalanceResult()
            }
        }
    }

    suspend fun getIncreaseBalanceOptions(): Either<DynamicCreditOption> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                paymentService.getIncreaseBalanceOptions(
                    accessibility = isAccessibilityEnable(),
                ).toDynamicCreditOption()
            }
        }
    }

    private fun getLanguage(): String {
        return ServiceLocator.getOrNull<String>(ServiceLocator.LANGUAGE) ?: "fa"
    }

    private fun isAccessibilityEnable(): Boolean {
        return ServiceLocator.isAccessibilityEnable()
    }

    private companion object {

        private val increaseBalanceRedirectUrl =
            "bazaar://${
                ServiceLocator.get<Context>().packageName
            }/increase_balance"
    }
}
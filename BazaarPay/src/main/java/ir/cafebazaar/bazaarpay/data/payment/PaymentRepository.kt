package ir.cafebazaar.bazaarpay.data.payment

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.DynamicCreditOption
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.pay.BalanceResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.InitCheckoutResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PurchaseStatus
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.utils.Either

internal class PaymentRepository {

    private val paymentRemoteDataSource: PaymentRemoteDataSource = ServiceLocator.get()

    suspend fun getPaymentMethods(): Either<PaymentMethodsInfo> {
        return paymentRemoteDataSource.getPaymentMethods()
    }

    suspend fun getMerchantInfo(): Either<MerchantInfo> {
        return paymentRemoteDataSource.getMerchantInfo()
    }

    suspend fun pay(
        paymentMethod: String,
        amount: Long? = null
    ): Either<PayResult> {
        return paymentRemoteDataSource.pay(paymentMethod, amount)
    }

    suspend fun commit(checkoutToken: String): Either<Unit> {
        return paymentRemoteDataSource.commit(checkoutToken).fold(
            ifSuccess = {
                Either.Success(Unit)
            },
            ifFailure = {
                Either.Failure(it)
            }
        )
    }

    suspend fun trace(checkoutToken: String): Either<PurchaseStatus> {
        return paymentRemoteDataSource.trace(checkoutToken)
    }

    suspend fun initCheckout(
        amount: Long,
        destination: String,
        serviceName: String
    ): Either<InitCheckoutResult> {
        return paymentRemoteDataSource.initCheckout(amount, destination, serviceName)
    }

    suspend fun getBalance(): Either<BalanceResult> {
        return paymentRemoteDataSource.getBalance()
    }

    suspend fun getIncreaseBalanceOptions(): Either<DynamicCreditOption> {
        return paymentRemoteDataSource.getIncreaseBalanceOptions()
    }
}
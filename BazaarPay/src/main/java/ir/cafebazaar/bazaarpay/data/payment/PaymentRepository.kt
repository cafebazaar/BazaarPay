package ir.cafebazaar.bazaarpay.data.payment

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType
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
        paymentMethod: PaymentMethodsType,
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
}
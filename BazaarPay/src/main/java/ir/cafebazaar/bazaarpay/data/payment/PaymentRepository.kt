package ir.cafebazaar.bazaarpay.data.payment

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType
import ir.cafebazaar.bazaarpay.utils.Either

internal class PaymentRepository {

    private val paymentRemoteDataSource: PaymentRemoteDataSource = ServiceLocator.get()

    fun getPaymentMethods(): Either<PaymentMethodsInfo> {
        return paymentRemoteDataSource.getPaymentMethods()
    }

    fun getMerchantInfo(): Either<MerchantInfo> {
        return paymentRemoteDataSource.getMerchantInfo()
    }

    fun pay(
        paymentMethod: PaymentMethodsType,
        amount: Long? = null
    ): Either<PayResult> {
        return paymentRemoteDataSource.pay(paymentMethod, amount)
    }
}
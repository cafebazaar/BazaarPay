package ir.cafebazaar.bazaarpay.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PurchaseStatus
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import ir.cafebazaar.bazaarpay.utils.doOnSuccess
import kotlinx.coroutines.launch

internal class BazaarPayViewModel : ViewModel() {

    private val paymentRepository = ServiceLocator.getOrNull<PaymentRepository>()

    private val _paymentSuccessLiveData = SingleLiveEvent<Unit>()
    val paymentSuccessLiveData: LiveData<Unit> = _paymentSuccessLiveData

    fun onActivityResumed() {
        traceCurrentPayment()
    }

    private fun traceCurrentPayment() = viewModelScope.launch {
        val checkoutToken = ServiceLocator.getOrNull<String>(ServiceLocator.CHECKOUT_TOKEN)
        if (checkoutToken != null) {
            paymentRepository?.trace(checkoutToken)?.doOnSuccess { status ->
                if (status == PurchaseStatus.PaidNotCommitted) {
                    _paymentSuccessLiveData.value = Unit
                }

            }
        }
    }
}
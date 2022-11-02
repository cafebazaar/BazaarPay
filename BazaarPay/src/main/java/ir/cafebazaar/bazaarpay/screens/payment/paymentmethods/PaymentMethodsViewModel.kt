package ir.cafebazaar.bazaarpay.screens.payment.paymentmethods

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.models.PaymentFlowState
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.screens.payment.increasecredit.DynamicCreditOptionDealerArg
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.launch

internal class PaymentMethodsViewModel : ViewModel() {

    private val context: Context = ServiceLocator.get()
    private val paymentRepository: PaymentRepository = ServiceLocator.get()
    private val paymentMethodsStateData = SingleLiveEvent<Resource<PaymentMethodsInfo>>()
    private val payStateData = SingleLiveEvent<Resource<PayResult>>()
    private val merchantInfoStateData = SingleLiveEvent<Resource<MerchantInfo>>()
    private val _paymentOptionViewLoaderLiveData = SingleLiveEvent<PaymentMethodViewLoader>()
    val paymentMethodViewLoaderLiveData: LiveData<PaymentMethodViewLoader> =
        _paymentOptionViewLoaderLiveData

    private val _navigationLiveData = SingleLiveEvent<NavDirections>()
    val navigationLiveData: LiveData<NavDirections> = _navigationLiveData

    fun getPaymentMethodsStateData(): LiveData<Resource<PaymentMethodsInfo>> =
        paymentMethodsStateData

    fun getPayStateData(): LiveData<Resource<PayResult>> =
        payStateData

    fun getMerchantInfoStateData(): LiveData<Resource<MerchantInfo>> = merchantInfoStateData

    fun loadData() {
        paymentMethodsStateData.value = Resource.loading()
        getMerchantInfo()
        getPaymentMethods()
    }

    private fun getPaymentMethods() {
        viewModelScope.launch {
            paymentRepository.getPaymentMethods().fold(
                ::handlePaymentMethodsSuccess,
                ::handlePaymentMethodsFailure
            )
        }
    }

    private fun getMerchantInfo() {
        viewModelScope.launch {
            paymentRepository.getMerchantInfo().fold(
                ::handleMerchantInfoSuccess,
                { }
            )
        }
    }

    private fun handleMerchantInfoSuccess(merchantInfo: MerchantInfo) {
        merchantInfoStateData.value =
            Resource(PaymentFlowState.MerchantInfo, merchantInfo)
    }

    private fun handlePaymentMethodsSuccess(paymentMethods: PaymentMethodsInfo) {
        paymentMethodsStateData.value =
            Resource(PaymentFlowState.PaymentMethodsInfo, paymentMethods)
    }

    private fun handlePaymentMethodsFailure(errorModel: ErrorModel) {
        paymentMethodsStateData.value = Resource.failed(failure = errorModel)
    }

    private fun handlePaySuccess(payResult: PayResult) {
        _navigationLiveData.value =
            PaymentMethodsFragmentDirections
                .openPaymentThankYouPageFragment(
                    isSuccess = true
                )
    }

    private fun handlePayFailure(errorModel: ErrorModel) {
        payStateData.value = Resource.failed(failure = errorModel)
    }

    fun onPaymentOptionClicked(selectedOptionPos: Int) {
        getPaymentInfo()?.paymentMethods?.getOrNull(selectedOptionPos)?.let { selectedMethod ->
            _paymentOptionViewLoaderLiveData.value = PaymentMethodViewLoader(
                price = selectedMethod.priceString,
                payButton = getPayButtonTextId(selectedMethod.methodType),
                subDescription = selectedMethod.subDescription
            )
        }
    }

    private fun getPayButtonTextId(type: PaymentMethodsType): Int {
        return when (type) {
            PaymentMethodsType.INCREASE_BALANCE -> {
                R.string.increase_balance
            }
            PaymentMethodsType.DIRECT_DEBIT_ACTIVATION -> {
                R.string.directdebit_signup
            }
            PaymentMethodsType.POSTPAID_CREDIT_ACTIVATION -> {
                R.string.postpaid_activation
            }
            PaymentMethodsType.POSTPAID_CREDIT -> {
                R.string.credit_pay
            }
            else -> {
                R.string.pay
            }
        }
    }

    private fun getPaymentInfo(): PaymentMethodsInfo? {
        return paymentMethodsStateData.value?.data
    }

    fun onPayButtonClicked(
        selectedPosition: Int
    ) {
        val paymentInfo = getPaymentInfo() ?: return
        val selectedOption = paymentInfo.paymentMethods[selectedPosition]
        when (selectedOption.methodType) {
            PaymentMethodsType.INCREASE_BALANCE -> {
                openIncreaseBalancePage()
            }
            PaymentMethodsType.DIRECT_DEBIT_ACTIVATION -> {
                openDirectDebitOnBoarding()
            }
            PaymentMethodsType.POSTPAID_CREDIT_ACTIVATION -> {
                openPostpaidTermsPage()
            }
            else -> {
                pay(selectedOption.methodType)
            }
        }
    }

    private fun pay(methodType: PaymentMethodsType) {
        payStateData.value = Resource.loading()
        viewModelScope.launch {
            paymentRepository.pay(methodType).fold(
                ::handlePaySuccess,
                ::handlePayFailure
            )
        }
    }

    private fun openIncreaseBalancePage() {
        paymentMethodsStateData.value?.data?.let { paymentMethodsStateData ->
            _navigationLiveData.value =
                paymentMethodsStateData.paymentMethods.first {
                    it.methodType == PaymentMethodsType.INCREASE_BALANCE
                }.priceString?.let { priceString ->
                    merchantInfoStateData.value?.data?.logoUrl?.let { logoUrl ->
                        merchantInfoStateData.value?.data?.accountName?.let { accountName ->
                            DynamicCreditOptionDealerArg(
                                iconUrl = logoUrl,
                                name = paymentMethodsStateData.destinationTitle,
                                info = accountName,
                                priceString = priceString
                            )
                        }
                    }
                }?.let { dynamicCreditOptionDealerArg ->
                    paymentMethodsStateData.dynamicCreditOption?.let { dynamicCreditOption ->
                        PaymentMethodsFragmentDirections
                            .actionPaymentMethodsFragmentToPaymentDynamicCreditFragment(
                                dynamicCreditOption,
                                dynamicCreditOptionDealerArg
                            )
                    }
                }
        }
    }

    private fun openPostpaidTermsPage() {
        _navigationLiveData.value =
            PaymentMethodsFragmentDirections.actionPaymentMethodsFragmentToPostpaidTermsFragment()
    }

    private fun openDirectDebitOnBoarding() {
        _navigationLiveData.value =
            PaymentMethodsFragmentDirections.actionPaymentMethodsFragmentToDirectDebitOnBoardingFragment()
    }
}
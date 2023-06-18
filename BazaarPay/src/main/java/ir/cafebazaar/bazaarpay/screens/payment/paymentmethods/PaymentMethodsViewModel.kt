package ir.cafebazaar.bazaarpay.screens.payment.paymentmethods

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.models.PaymentFlowState
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.screens.payment.increasecredit.DynamicCreditOptionDealerArg
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsFragment.Companion.CLICK_PAY_PUTTON
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsFragment.Companion.PAYMENT_METHODES
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsFragment.Companion.SCREEN_NAME
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsFragment.Companion.SELECTED_METHODE
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.launch

internal class PaymentMethodsViewModel : ViewModel() {

    private val paymentRepository: PaymentRepository = ServiceLocator.get()
    private val accountRepository: AccountRepository = ServiceLocator.get()
    private val paymentMethodsStateData = SingleLiveEvent<Resource<PaymentMethodsInfo>>()
    private val payStateData = SingleLiveEvent<Resource<PayResult>>()
    private val merchantInfoStateData = SingleLiveEvent<Resource<MerchantInfo>>()
    private val _paymentOptionViewLoaderLiveData = SingleLiveEvent<PaymentMethodViewLoader>()
    val paymentMethodViewLoaderLiveData: LiveData<PaymentMethodViewLoader> =
        _paymentOptionViewLoaderLiveData

    private val _accountInfoLiveData = SingleLiveEvent<String>()
    val accountInfoLiveData: LiveData<String> = _accountInfoLiveData

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
        getAccountData()
    }

    private fun getAccountData() {
        _accountInfoLiveData.value = accountRepository.getPhone()
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
            Analytics.sendClickEvent(where = SCREEN_NAME, what = selectedMethod.methodTypeString)
            _paymentOptionViewLoaderLiveData.value = PaymentMethodViewLoader(
                price = selectedMethod.priceString,
                payButton = getPayButtonTextId(selectedMethod.methodType),
                subDescription = selectedMethod.subDescription,
                enabled = selectedMethod.enabled
            )
        }
    }

    private fun getPayButtonTextId(type: PaymentMethodsType): Int {
        return when (type) {
            PaymentMethodsType.INCREASE_BALANCE -> {
                R.string.bazaarpay_increase_balance
            }

            PaymentMethodsType.DIRECT_DEBIT_ACTIVATION -> {
                R.string.bazaarpay_directdebit_signup
            }

            PaymentMethodsType.POSTPAID_CREDIT_ACTIVATION -> {
                R.string.bazaarpay_postpaid_activation
            }

            PaymentMethodsType.POSTPAID_CREDIT -> {
                R.string.bazaarpay_credit_pay
            }

            else -> {
                R.string.bazaarpay_pay
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
        Analytics.sendClickEvent(
            where = SCREEN_NAME,
            what = CLICK_PAY_PUTTON,
            extra = hashMapOf(
                SELECTED_METHODE to selectedOption.methodTypeString,
                PAYMENT_METHODES to getMethodeTypes()
            )
        )
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
                pay(selectedOption.methodTypeString)
            }
        }
    }

    private fun pay(methodType: String) {
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
            paymentMethodsStateData.paymentMethods.firstOrNull {
                it.methodType == PaymentMethodsType.INCREASE_BALANCE
            }?.priceString?.let { priceString ->
                val merchantLogo = merchantInfoStateData.value?.data?.logoUrl
                val merchantAccountName = merchantInfoStateData.value?.data?.accountName
                val dynamicCreditOptionDealerArg = DynamicCreditOptionDealerArg(
                    iconUrl = merchantLogo,
                    name = paymentMethodsStateData.destinationTitle,
                    info = merchantAccountName,
                    priceString = priceString
                )
                val dynamicCreditOption = paymentMethodsStateData.dynamicCreditOption
                if (dynamicCreditOption != null) {
                    val nav = PaymentMethodsFragmentDirections
                        .actionPaymentMethodsFragmentToPaymentDynamicCreditFragment(
                            dynamicCreditOption,
                            dynamicCreditOptionDealerArg
                        )
                    _navigationLiveData.value = nav
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

    private fun getMethodeTypes(): String {
        return getPaymentInfo()?.paymentMethods?.map { it.methodTypeString }.toString()
    }
}
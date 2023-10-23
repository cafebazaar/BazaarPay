package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.analytics.Analytics.WHAT_KEY
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.DynamicCreditOption
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.extensions.digits
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.extensions.toPriceFormat
import ir.cafebazaar.bazaarpay.extensions.toToman
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.screens.payment.increasecredit.PaymentDynamicCreditFragment.Companion.AMOUNT_OPTION
import ir.cafebazaar.bazaarpay.screens.payment.increasecredit.PaymentDynamicCreditFragment.Companion.CLICK_AMOUNT_OPTION
import ir.cafebazaar.bazaarpay.screens.payment.increasecredit.PaymentDynamicCreditFragment.Companion.SCREEN_NAME
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.Locale

internal class DynamicCreditViewModel : ViewModel() {

    private val paymentRepository: PaymentRepository by lazy { ServiceLocator.get() }

    private val _editTextValueLiveData = MutableLiveData<String?>()
    val editTextValueLiveData: LiveData<String?> = _editTextValueLiveData

    private val _itemChangedLiveData = MutableLiveData<Int>()
    val itemChangedLiveData: LiveData<Int> = _itemChangedLiveData

    private val _dynamicCreditLiveData = MutableLiveData<Resource<DynamicCreditOption>>()
    val dynamicCreditLiveData: LiveData<Resource<DynamicCreditOption>> = _dynamicCreditLiveData

    private val _actionLiveData = MutableLiveData<Resource<String>>()
    val actionLiveData: LiveData<Resource<String>> = _actionLiveData

    private val _errorLiveData = SingleLiveEvent<Pair<Int, Long?>>()
    val errorLiveData: LiveData<Pair<Int, Long?>> = _errorLiveData

    var preText: String? = null
    private var _creditOptions: DynamicCreditOption? = null
    private val creditOptions: DynamicCreditOption?
        get() = _creditOptions

    fun onTextChanged(changeValue: String) {
        val currentValue = preText.orEmpty()
        val priceString = when {
            changeValue.length > currentValue.length -> {
                getPriceFromString(changeValue)
            }

            changeValue.length < currentValue.length -> {
                getPriceWhenCurrentValueBiggerThanChanged(currentValue, changeValue)
            }

            else -> {
                getPriceFromString(changeValue)
            }
        }

        val textValue = when {
            priceIsZero(priceString) -> {
                null
            }

            priceIsBiggerThanMaximum(priceString) -> {
                _errorLiveData.value = Pair(
                    R.string.bazaarpay_dynamic_credit_exceed_amount,
                    creditOptions?.maxAvailableAmount?.toToman()
                )
                val maxValue = requireNotNull(creditOptions).maxAvailableAmount.toToman()
                getPriceFromString(maxValue.toString())
            }

            else -> {
                priceString
            }
        }
        _editTextValueLiveData.value = textValue
        textValue?.let { newValue ->
            selectDynamicItemBasedOnInputValue(newValue)
        }
        preText = textValue
    }

    private fun showData(options: DynamicCreditOption) {
        _dynamicCreditLiveData.value = Resource.loaded(options)
        _editTextValueLiveData.value = getPriceFromString(
            (options.defaultAmount / TOMAN_TO_RIAL_FACTOR).toString()
        )?.also { priceValue ->
            selectDynamicItemBasedOnInputValue(priceValue)
        }
    }

    fun onBackClicked() {
        creditOptions?.options?.forEach { it.isSelected = false }
    }

    fun onPayButtonClicked(priceString: String, type: IncreaseCreditType) {
        if (priceString.isEmpty()) {
            return
        }

        if (!enoughAmount(priceString)) {
            _errorLiveData.value = Pair(
                R.string.bazaarpay_dynamic_credit_not_enough,
                creditOptions?.minAvailableAmount?.toToman()
            )
            return
        }

        increaseCredit(priceString, type)
    }

    fun onDynamicItemClicked(position: Int) {
        creditOptions?.options?.forEachIndexed { pos, item ->
            val preSelectedState = item.isSelected
            item.isSelected = position == pos

            if (item.isSelected != preSelectedState) {
                _itemChangedLiveData.value = pos
            }
        }

        val amount = creditOptions
            ?.options
            ?.get(position)
            ?.amount ?: 0
        _editTextValueLiveData.value = getPriceFromString((amount.toToman()).toString())

        Analytics.sendClickEvent(
            where = SCREEN_NAME,
            what = hashMapOf(
                WHAT_KEY to CLICK_AMOUNT_OPTION,
                AMOUNT_OPTION to amount.toString()
            )
        )
    }

    private fun selectDynamicItemBasedOnInputValue(newValue: String) {
        val newDigitValue = newValue.digits() * TOMAN_TO_RIAL_FACTOR
        creditOptions?.options?.forEachIndexed { pos, item ->
            val shouldItemSelected = item.amount == newDigitValue
            if (item.isSelected != shouldItemSelected) {
                item.isSelected = shouldItemSelected
                _itemChangedLiveData.value = pos
            }
        }
    }

    private fun priceIsBiggerThanMaximum(priceString: String?): Boolean {
        val priceDigit = priceString?.digits() ?: 0
        val maximumValue = creditOptions?.maxAvailableAmount ?: Long.MAX_VALUE
        return priceDigit > maximumValue
    }

    private fun priceIsZero(priceString: String?): Boolean {
        return priceString?.digits() == 0L
    }

    private fun getPriceWhenCurrentValueBiggerThanChanged(
        currentValue: String,
        changeValue: String
    ): String? {
        val currentDigit = currentValue.digits()
        val changedDigit = changeValue.digits()

        return when {
            currentDigit.toString() == changeValue -> {
                // duplicate value
                getPriceFromString(changeValue)
            }

            currentDigit == changedDigit -> {
                // removed from last
                getPriceIfBiggerThanZero(currentDigit / TOMAN_TO_RIAL_FACTOR)
            }

            else -> {
                getPriceIfBiggerThanZero(changedDigit)
            }
        }
    }

    private fun getPriceIfBiggerThanZero(price: Long): String? {
        return if (price == 0L) {
            null
        } else {
            price.toPriceFormat(Locale("fa"))
        }
    }

    private fun getPriceFromString(changeValue: String): String? {
        return getPriceIfBiggerThanZero(changeValue.digits())
    }

    private fun enoughAmount(priceString: String): Boolean {
        val amount = priceString.digits() * TOMAN_TO_RIAL_FACTOR
        val neededChargeAmount = creditOptions?.minAvailableAmount ?: Long.MAX_VALUE
        return amount >= neededChargeAmount
    }

    private fun increaseCredit(priceString: String, type: IncreaseCreditType) {
        check(creditOptions != null) {
            "invalid state"
        }

        _actionLiveData.value = Resource.loading()
        viewModelScope.launch {
            if (type == IncreaseCreditType.INCREASE) {
                increaseBalance(priceString)
            } else {
                pay(priceString)
            }
        }
    }

    private suspend fun pay(priceString: String) {
        paymentRepository.pay(
            PaymentMethodsType.INCREASE_BALANCE.value,
            (priceString.digits() * TOMAN_TO_RIAL_FACTOR)
        ).fold(ifSuccess = ::onIncreaseBalanceSuccess, ifFailure = ::onIncreaseBalanceFailed)
    }

    private suspend fun increaseBalance(priceString: String) {
        paymentRepository.increaseBalance(
            (priceString.digits() * TOMAN_TO_RIAL_FACTOR)
        ).fold(ifSuccess = ::onIncreaseBalanceSuccess, ifFailure = ::onIncreaseBalanceFailed)
    }

    private fun onIncreaseBalanceSuccess(result: PayResult) {
        _actionLiveData.value = Resource.loaded(result.redirectUrl)
    }

    private fun onIncreaseBalanceFailed(error: ErrorModel) {
        _actionLiveData.value = Resource.failed(failure = error)
    }

    fun initArgs(creditOptionsArgs: DynamicCreditOption?) {
        if (creditOptionsArgs != null) {
            showData(options = creditOptionsArgs)
            _creditOptions = creditOptionsArgs
            _dynamicCreditLiveData.value = Resource.loaded(creditOptionsArgs)
        } else {
            fetchDynamicCreditOption()
        }
    }

    fun onRetryClick(creditOptionsArgs: DynamicCreditOption?) {
        initArgs(creditOptionsArgs)
    }

    private fun fetchDynamicCreditOption() = viewModelScope.launch {
        _dynamicCreditLiveData.value = Resource.loading()
        paymentRepository.getIncreaseBalanceOptions().fold(
            ifSuccess = {
                _creditOptions = it
                _dynamicCreditLiveData.value = Resource.loaded(data = it)
                showData(options = it)
            },
            ifFailure = {
                _dynamicCreditLiveData.value = Resource.failed(failure = it)

            }
        )
    }

    private companion object {

        const val TOMAN_TO_RIAL_FACTOR = 10
    }
}

internal enum class IncreaseCreditType {
    PAY, INCREASE
}
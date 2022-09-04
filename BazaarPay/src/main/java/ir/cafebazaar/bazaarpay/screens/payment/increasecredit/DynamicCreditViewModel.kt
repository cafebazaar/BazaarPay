package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.DynamicCreditOption
import ir.cafebazaar.bazaarpay.extensions.digits
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.extensions.toPriceFormat
import ir.cafebazaar.bazaarpay.extensions.toToman
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*

internal class DynamicCreditViewModel : ViewModel() {

    private val context: Context = ServiceLocator.get()
    private val paymentRepository: PaymentRepository = ServiceLocator.get()
    private val globalDispatchers: GlobalDispatchers = ServiceLocator.get()

    private val _editTextValueLiveData = MutableLiveData<String?>()
    val editTextValueLiveData: LiveData<String?> = _editTextValueLiveData

    private val _itemChangedLiveData = MutableLiveData<Int>()
    val itemChangedLiveData: LiveData<Int> = _itemChangedLiveData

    private val _dynamicCreditLiveData = MutableLiveData<Resource<DynamicCreditOption>>()
    val dynamicCreditLiveData: LiveData<Resource<DynamicCreditOption>> = _dynamicCreditLiveData

    private val _actionLiveData = MutableLiveData<Resource<String>>()
    val actionLiveData: LiveData<Resource<String>> = _actionLiveData

    private val _navigationLiveData = SingleLiveEvent<NavDirections>()
    val navigationLiveData: LiveData<NavDirections> = _navigationLiveData

    private val _errorLiveData = SingleLiveEvent<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    var preText: String? = null
    private var creditOptionsRef: WeakReference<DynamicCreditOption>? = null
    private val creditOptions: DynamicCreditOption?
        get() = creditOptionsRef?.get()

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
                _errorLiveData.value = context.getString(
                    R.string.dynamic_credit_exceed_amount,
                    creditOptions?.maxAvailableAmount
                )
                val maxValue = requireNotNull(creditOptions).maxAvailableAmount
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

    fun onViewCreated(options: DynamicCreditOption) {
        showData(options)
    }

    private fun showData(options: DynamicCreditOption) {
        this.creditOptionsRef = WeakReference(options)
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

    fun onPayButtonClicked(priceString: String) {
        if (priceString.isEmpty()) {
            return
        }

        if (!enoughAmount(priceString)) {
            _errorLiveData.value = context.getString(
                R.string.dynamic_credit_not_enough, creditOptions?.minAvailableAmount
            )
            return
        }

        increaseCredit(priceString)
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
            price.toPriceFormat(context, Locale(ServiceLocator.get(ServiceLocator.LANGUAGE)))
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

    private fun increaseCredit(priceString: String) {
        check(creditOptions != null) {
            "invalid state"
        }

        _actionLiveData.value = Resource.loading()
        viewModelScope.launch(globalDispatchers.iO) {
            paymentRepository.pay(
                PaymentMethodsType.INCREASE_BALANCE,
                (priceString.digits() * TOMAN_TO_RIAL_FACTOR)
            ).fold(
                {
                    _actionLiveData.postValue(
                        Resource.loaded(it.redirectUrl)
                    )
                },
                {
                    _actionLiveData.postValue(
                        Resource.failed(
                            failure = it
                        )
                    )
                }
            )
        }
    }

    private companion object {

        const val TOMAN_TO_RIAL_FACTOR = 10
    }
}
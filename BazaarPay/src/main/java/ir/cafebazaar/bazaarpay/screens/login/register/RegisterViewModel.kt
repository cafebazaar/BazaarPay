package ir.cafebazaar.bazaarpay.screens.login.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.extensions.isValidPhoneNumber
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.data.bazaar.models.InvalidPhoneNumberException
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class RegisterViewModel : ViewModel() {

    private val accountRepository: AccountRepository = ServiceLocator.get()
    private val globalDispatchers: GlobalDispatchers = ServiceLocator.get()

    private val _data = SingleLiveEvent<Resource<WaitingTimeWithEnableCall>>()
    val data: LiveData<Resource<WaitingTimeWithEnableCall>> = _data

    private val _savedPhones = MutableLiveData<List<String>>()
    val savedPhones: LiveData<List<String>> = _savedPhones

    fun loadSavedPhones() {
        viewModelScope.launch {
            _savedPhones.value = accountRepository.getAutoFillPhones()
        }
    }

    fun register(phoneNumber: String) {
        if (phoneNumber.isValidPhoneNumber()) {
            _data.value = Resource.loading()
            viewModelScope.launch {
                accountRepository.getOtpToken(phoneNumber).fold(::success,::error)
            }
        } else {
            error(InvalidPhoneNumberException())
        }
    }

    private fun error(throwable: ErrorModel) {
        _data.value = Resource.failed(failure = throwable)
    }

    private fun success(
        response: WaitingTimeWithEnableCall
    ) {
        _data.value = Resource.loaded(response)
    }
}
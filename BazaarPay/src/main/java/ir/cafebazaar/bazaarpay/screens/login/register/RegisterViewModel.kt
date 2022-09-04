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

    private val data = SingleLiveEvent<Resource<WaitingTimeWithEnableCall>>()
    private val savedPhones = MutableLiveData<List<String>>()

    fun getData(): LiveData<Resource<WaitingTimeWithEnableCall>> = data

    fun getSavedPhones(): LiveData<List<String>> = savedPhones

    fun loadSavedPhones() {
        viewModelScope.launch {
            withContext(globalDispatchers.iO) {
                savedPhones.postValue(accountRepository.getAutoFillPhones())
            }
        }
    }

    fun register(phoneNumber: String) {
        if (phoneNumber.isValidPhoneNumber()) {
            data.postValue(Resource.loading())
            viewModelScope.launch {
                withContext(globalDispatchers.iO) {
                    accountRepository.getOtpToken(phoneNumber).fold(::success,::error)
                }
            }
        } else {
            error(InvalidPhoneNumberException())
        }
    }

    private fun error(throwable: ErrorModel) {
        data.postValue(Resource.failed(failure = throwable))
    }

    private fun success(
        response: WaitingTimeWithEnableCall
    ) {
        data.postValue(Resource.loaded(response))
    }
}
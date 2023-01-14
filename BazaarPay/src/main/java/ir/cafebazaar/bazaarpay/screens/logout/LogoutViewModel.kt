package ir.cafebazaar.bazaarpay.screens.logout

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent

internal class LogoutViewModel : ViewModel() {

    private val accountRepository: AccountRepository by lazy {
        ServiceLocator.get()
    }

    private val _navigationLiveData = SingleLiveEvent<NavDirections>()
    val navigationLiveData: LiveData<NavDirections> = _navigationLiveData

    fun onLogoutClicked() {
        accountRepository.logout()
        _navigationLiveData.value = LogoutFragmentDirections.openSignin()
    }
}
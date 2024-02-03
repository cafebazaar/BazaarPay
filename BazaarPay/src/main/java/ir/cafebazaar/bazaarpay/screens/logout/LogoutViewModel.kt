package ir.cafebazaar.bazaarpay.screens.logout

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.analytics.Analytics.WHAT_KEY
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.screens.logout.LogoutFragment.Companion.CLICK_LOG_OUT
import ir.cafebazaar.bazaarpay.screens.logout.LogoutFragment.Companion.SCREEN_NAME
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent

internal class LogoutViewModel : ViewModel() {

    private val accountRepository: AccountRepository by lazy {
        ServiceLocator.get()
    }

    private val _navigationLiveData = SingleLiveEvent<NavDirections>()
    val navigationLiveData: LiveData<NavDirections> = _navigationLiveData

    fun onLogoutClicked() {
        Analytics.sendClickEvent(where = SCREEN_NAME, what = hashMapOf(WHAT_KEY to CLICK_LOG_OUT))
        accountRepository.logout()
        _navigationLiveData.value = LogoutFragmentDirections.openSignin()
    }
}
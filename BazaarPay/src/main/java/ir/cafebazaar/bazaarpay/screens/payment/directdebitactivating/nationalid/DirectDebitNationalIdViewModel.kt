package ir.cafebazaar.bazaarpay.screens.payment.directdebitactivating.nationalid

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import ir.cafebazaar.bazaarpay.extensions.isValidNationalId
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent

internal class DirectDebitNationalIdViewModel : ViewModel() {

    private val _showInvalidErrorLiveData = SingleLiveEvent<Unit>()
    val showInvalidErrorLiveData: LiveData<Unit> = _showInvalidErrorLiveData

    private val _navigationLiveData = SingleLiveEvent<NavDirections>()
    val navigationLiveData: LiveData<NavDirections> = _navigationLiveData

    fun onAcceptClicked(nationalId: String) {
        if (nationalId.isValidNationalId()) {
            _navigationLiveData.value =
                DirectDebitNationalIdFragmentDirections
                    .actionNationalIdFragmentToDirectDebitBankListFragment(nationalId)
        } else {
            _showInvalidErrorLiveData.call()
        }
    }
}
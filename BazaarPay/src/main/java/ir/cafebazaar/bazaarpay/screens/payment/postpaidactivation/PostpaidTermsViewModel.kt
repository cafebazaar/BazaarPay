package ir.cafebazaar.bazaarpay.screens.payment.postpaidactivation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarPaymentRepository
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.launch

internal class PostpaidTermsViewModel : ViewModel() {

    private val globalDispatchers: GlobalDispatchers = ServiceLocator.get()
    private val bazaarPaymentRepository: BazaarPaymentRepository = ServiceLocator.get()

    private val _activationLiveData = SingleLiveEvent<Resource<Unit>>()
    val activationLiveData: LiveData<Resource<Unit>> = _activationLiveData

    fun acceptButtonClicked() {
        _activationLiveData.value = Resource.loading()
        viewModelScope.launch(globalDispatchers.iO) {
            bazaarPaymentRepository.activatePostPaidCredit().fold(
                {
                    _activationLiveData.postValue(
                        Resource.loaded()
                    )
                },
                { error ->
                    _activationLiveData.postValue(
                        Resource.failed(
                            failure = error
                        )
                    )
                }
            )
        }
    }
}
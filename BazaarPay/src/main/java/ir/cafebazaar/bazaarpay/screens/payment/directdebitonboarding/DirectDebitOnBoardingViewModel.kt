package ir.cafebazaar.bazaarpay.screens.payment.directdebitonboarding

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.models.PaymentFlowState
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarPaymentRepository
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DirectDebitOnBoardingViewModel : ViewModel() {

    private val bazaarPaymentRepository: BazaarPaymentRepository = ServiceLocator.get()
    private val globalDispatchers: GlobalDispatchers = ServiceLocator.get()

    private val _onBoardingItemsLiveData = MutableLiveData<Resource<DirectDebitOnBoardingDetails>>()
    val directDebitOnBoardingStates: LiveData<Resource<DirectDebitOnBoardingDetails>> =
        _onBoardingItemsLiveData

    private val _navigationLiveData = SingleLiveEvent<NavDirections>()
    val navigationLiveData: LiveData<NavDirections> = _navigationLiveData

    fun loadData() {
        _onBoardingItemsLiveData.value = Resource.loading()
        viewModelScope.launch {
            withContext(globalDispatchers.iO) {
                bazaarPaymentRepository.getDirectDebitOnBoarding()
                    .fold(::success, ::error)
            }
        }
    }

    private fun success(response: DirectDebitOnBoardingDetails) {
        _onBoardingItemsLiveData.postValue(
            Resource(PaymentFlowState.DirectDebitObBoardingDetails, response)
        )

    }

    private fun error(error: ErrorModel) {
        _onBoardingItemsLiveData.postValue(
            Resource.failed(failure = error)
        )
    }

    fun onSkipButtonClicked() {
        _navigationLiveData.value =
            DirectDebitOnBoardingFragmentDirections.actionDirectDebitOnBoardingFragmentToNationalIdFragment()
    }
}
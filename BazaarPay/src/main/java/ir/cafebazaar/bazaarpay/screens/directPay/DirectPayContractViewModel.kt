package ir.cafebazaar.bazaarpay.screens.directPay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.directPay.DirectPayRemoteDataSource
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractAction
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractResponse
import ir.cafebazaar.bazaarpay.extensions.ServiceType
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.extensions.makeErrorModelFromNetworkResponse
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import kotlinx.coroutines.launch
import retrofit2.Response

internal class DirectPayContractViewModel : ViewModel() {

    private val directPayRemoteDataSource: DirectPayRemoteDataSource = ServiceLocator.get()

    private val _contractLiveData = MutableLiveData<Resource<DirectPayContractResponse>>()
    val contractLiveData: LiveData<Resource<DirectPayContractResponse>> = _contractLiveData

    private val _contractActionLiveData = MutableLiveData<Pair<Resource<Unit>, DirectPayContractAction>>()
    val contractActionLiveData: LiveData<Pair<Resource<Unit>, DirectPayContractAction>> = _contractActionLiveData

    fun loadData(contractToken: String) {
        _contractLiveData.value = Resource.loading()
        viewModelScope.launch {
            directPayRemoteDataSource.getDirectPayContract(contractToken).fold(::success, ::error)
        }
    }

    fun finalizeContract(contractToken: String, action: DirectPayContractAction) {
        _contractActionLiveData.value = Resource.loading<Unit>() to action
        viewModelScope.launch {
            directPayRemoteDataSource.finalizedContract(contractToken, action)
                .fold(ifSuccess = { response ->
                    onSuccessFinalize(action, response)
                }, ifFailure = { error ->
                    onErrorFinalize(action, error)
                })
        }
    }

    private fun onSuccessFinalize(action: DirectPayContractAction, response: Response<Unit>) {
        if (response.isSuccessful) {
            _contractActionLiveData.value = Resource(ResourceState.Success, Unit) to action
        } else {
            val error = makeErrorModelFromNetworkResponse(
                response.errorBody().toString(),
                ServiceType.BAZAARPAY
            )
            onErrorFinalize(action, error)
        }
    }

    private fun onErrorFinalize(action: DirectPayContractAction, error: ErrorModel) {
        _contractActionLiveData.value = Resource.failed<Unit>(failure = error) to action
    }

    private fun success(response: DirectPayContractResponse) {
        _contractLiveData.value = Resource(ResourceState.Success, response)
    }

    private fun error(error: ErrorModel) {
        _contractLiveData.value = Resource.failed(failure = error)
    }
}
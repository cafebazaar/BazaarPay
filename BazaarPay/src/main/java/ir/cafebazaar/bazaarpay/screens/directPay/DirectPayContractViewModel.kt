package ir.cafebazaar.bazaarpay.screens.directPay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.directPay.DirectPayRemoteDataSource
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractResponse
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import kotlinx.coroutines.launch

internal class DirectPayContractViewModel : ViewModel() {

    private val directPayRemoteDataSource: DirectPayRemoteDataSource = ServiceLocator.get()

    private val _contractLiveData = MutableLiveData<Resource<DirectPayContractResponse>>()
    val contractLiveData: LiveData<Resource<DirectPayContractResponse>> = _contractLiveData

    fun loadData(contractToken: String) {
        _contractLiveData.value = Resource.loading()
        viewModelScope.launch {
            directPayRemoteDataSource.getDirectPayContract(contractToken).fold(::success, ::error)
        }
    }

    private fun success(response: DirectPayContractResponse) {
        _contractLiveData.value = Resource(ResourceState.Success, response)
    }

    private fun error(error: ErrorModel) {
        _contractLiveData.value = Resource.failed(failure = error)
    }
}
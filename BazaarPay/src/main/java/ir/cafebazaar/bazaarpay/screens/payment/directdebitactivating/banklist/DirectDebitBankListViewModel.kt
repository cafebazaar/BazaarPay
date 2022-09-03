package ir.cafebazaar.bazaarpay.screens.payment.directdebitactivating.banklist

import androidx.lifecycle.*
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarRepository
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.Bank
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.ContractCreation
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.launch

internal open class DirectDebitBankListViewModel : ViewModel() {


    private val data: MutableList<BankList> = mutableListOf()
    private val bazaarRepository: BazaarRepository = ServiceLocator.get()
    private val globalDispatchers: GlobalDispatchers = ServiceLocator.get()

    private val _enableActionButtonStateLiveData = SingleLiveEvent<Boolean>()
    val enableActionButtonStateLiveData: LiveData<Boolean> = _enableActionButtonStateLiveData

    private val _registerDirectDebitLiveData = SingleLiveEvent<Resource<String>>()
    val registerDirectDebitLiveData: LiveData<Resource<String>> = _registerDirectDebitLiveData

    private val _notifyLiveData = SingleLiveEvent<Int>()
    val notifyLiveData: LiveData<Int> = _notifyLiveData

    private val _dataLiveData = MutableLiveData<Resource<List<BankList>>>()
    val dataLiveData: LiveData<Resource<List<BankList>>> = _dataLiveData

    fun loadData() {
        viewModelScope.launch(globalDispatchers.iO) {
            bazaarRepository.getAvailableBanks()
                .fold(::handleSuccessBankList, ::error)
        }
    }

    private fun handleSuccessBankList(response: AvailableBanks) {
        val items = prepareRowItems(
            response.banks
        )
        data.addAll(items)
        _dataLiveData.postValue(
            Resource.loaded(
                data
            )
        )
    }

    private fun error(error: ErrorModel) {
        _dataLiveData.postValue(
            Resource.failed(failure = error)
        )
    }

    private fun prepareRowItems(banks: List<Bank>): List<BankList> {
        val bankItems = banks.map { bankModel ->
            BankList.BankListRowItem(
                model = bankModel,
                onItemSelected = ::onBankRowSelected
            )
        }
        return mutableListOf<BankList>().also { items ->
            items.add(BankList.BankListHeaderItem)
            items.addAll(bankItems)
        }
    }

    private fun onBankRowSelected(item: BankList.BankListRowItem) {
        if (item.isSelected.not()) {
            _enableActionButtonStateLiveData.postValue(true)

            data.forEachIndexed { index, bankData ->
                if (bankData is BankList.BankListRowItem) {
                    if (item.model.code == bankData.model.code) {
                        bankData.isSelected = true
                        viewModelScope.launch(globalDispatchers.main) {
                            _notifyLiveData.value = index
                        }
                    } else if (bankData.isSelected) {
                        bankData.isSelected = false
                        viewModelScope.launch(globalDispatchers.main) {
                            _notifyLiveData.value = index
                        }
                    }
                }
            }
        }
    }

    fun onRegisterClicked(
        nationalId: String
    ) {
        getSelectedBankItem()?.let { selectedItem ->
            viewModelScope.launch(globalDispatchers.iO) {
                _registerDirectDebitLiveData.postValue(Resource.loading())
                startRegistering(selectedItem, nationalId)
            }
        }
    }

    private fun startRegistering(
        selectedItem: BankList.BankListRowItem,
        nationalId: String
    ) {
        viewModelScope.launch {
            bazaarRepository.getDirectDebitContractCreationUrl(
                bankCode = selectedItem.model.code,
                nationalId
            ).fold(::registerSucceed, ::registerFailed)
        }
    }

    private fun registerSucceed(url: ContractCreation) {
        clearSelectedBankItem()
        _registerDirectDebitLiveData.postValue(
            Resource.loaded(
                data = url.url
            )
        )
    }

    private fun clearSelectedBankItem() {
        data.forEachIndexed { index, data ->
            if (data is BankList.BankListRowItem) {
                if (data.isSelected) {
                    data.isSelected = false
                    _notifyLiveData.postValue(index)
                }
            }
        }
        _enableActionButtonStateLiveData.postValue(false)
    }

    private fun registerFailed(errorModel: ErrorModel) {
        _registerDirectDebitLiveData.postValue(
            Resource.failed(
                failure = errorModel
            )
        )
    }

    private fun getSelectedBankItem(): BankList.BankListRowItem? {
        return data.filterIsInstance<BankList.BankListRowItem>()
            .firstOrNull { it.isSelected }
    }
}
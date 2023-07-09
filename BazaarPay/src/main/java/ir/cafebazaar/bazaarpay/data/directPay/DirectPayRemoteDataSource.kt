package ir.cafebazaar.bazaarpay.data.directPay

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.directPay.api.DirectPayService
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractResponse
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.withContext

internal class DirectPayRemoteDataSource {

    private val directPayService: DirectPayService by lazy {
        ServiceLocator.get()
    }

    private val globalDispatchers: GlobalDispatchers by lazy {
        ServiceLocator.get()
    }

    suspend fun getDirectPayContract(contractToken: String): Either<DirectPayContractResponse> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall() {
                directPayService.getDirectPayContract(contractToken)
                    .toDirectPayContract()
            }
        }
    }
}
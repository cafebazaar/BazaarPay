package ir.cafebazaar.bazaarpay.data.directPay

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.directPay.api.DirectPayService
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractAction
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractResponse
import ir.cafebazaar.bazaarpay.data.directPay.model.FinalizeDirectPayContractRequest
import ir.cafebazaar.bazaarpay.extensions.ServiceType
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.withContext
import retrofit2.Response

internal class DirectPayRemoteDataSource {

    private val directPayService: DirectPayService by lazy {
        ServiceLocator.get()
    }

    private val globalDispatchers: GlobalDispatchers by lazy {
        ServiceLocator.get()
    }

    suspend fun getDirectPayContract(contractToken: String): Either<DirectPayContractResponse> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(serviceType = ServiceType.BAZAARPAY) {
                directPayService.getDirectPayContract(contractToken)
                    .toDirectPayContract()
            }
        }
    }

    suspend fun finalizedContract(
        contractToken: String,
        action: DirectPayContractAction
    ): Either<Response<Unit>> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(serviceType = ServiceType.BAZAARPAY) {
                directPayService.finalizeContract(
                    FinalizeDirectPayContractRequest(
                        contractToken = contractToken,
                        action = action.value
                    )
                )
            }
        }
    }
}
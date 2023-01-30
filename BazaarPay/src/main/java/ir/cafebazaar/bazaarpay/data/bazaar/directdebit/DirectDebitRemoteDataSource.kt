package ir.cafebazaar.bazaarpay.data.bazaar.directdebit

import android.content.Context
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.directdebit.api.DirectDebitService
import ir.cafebazaar.bazaarpay.data.bazaar.directdebit.contractcreation.ContractCreation
import ir.cafebazaar.bazaarpay.data.bazaar.directdebit.contractcreation.request.GetDirectDebitContractCreationUrlRequestDto
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.extensions.ServiceType
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.withContext

internal class DirectDebitRemoteDataSource {

    private val directDebitService: DirectDebitService by lazy {
        ServiceLocator.get()
    }

    private val globalDispatchers: GlobalDispatchers by lazy {
        ServiceLocator.get()
    }

    suspend fun getCreateContractUrl(
        bankCode: String,
        nationalId: String
    ): Either<ContractCreation> {
        return withContext(globalDispatchers.iO) {
            val language = ServiceLocator.get<String>(ServiceLocator.LANGUAGE)
            return@withContext safeApiCall {
                directDebitService.getCreateContractUrl(
                    GetDirectDebitContractCreationUrlRequestDto(
                        bankCode = bankCode,
                        nationalId = nationalId,
                        redirectUrl = DIRECT_DEBIT_ACTIVATION_REDIRECT_URL
                    ),
                    language
                ).toContractCreation()
            }
        }
    }

    suspend fun getAvailableBanks(): Either<AvailableBanks> {
        val language = ServiceLocator.get<String>(ServiceLocator.LANGUAGE)
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                directDebitService.getAvailableBanks(
                    language
                ).toAvailableBanks()
            }
        }
    }

    private companion object {
        var DIRECT_DEBIT_ACTIVATION_REDIRECT_URL =
            "bazaar://${
                ServiceLocator.get<Context>().packageName
            }/direct_debit_activation"
    }
}
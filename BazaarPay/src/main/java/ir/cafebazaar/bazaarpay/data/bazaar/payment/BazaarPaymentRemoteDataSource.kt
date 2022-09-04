package ir.cafebazaar.bazaarpay.data.bazaar.payment

import android.content.Context
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.payment.api.BazaarPaymentService
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.request.GetAvailableBanksSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.ContractCreation
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.request.GetDirectDebitContractCreationUrlSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.request.GetDirectDebitOnBoardingSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.request.ActivatePostpaidCreditSingleRequest
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.withContext

internal class BazaarPaymentRemoteDataSource {

    private val bazaarService: BazaarPaymentService by lazy {
        ServiceLocator.get()
    }

    private val globalDispatchers: GlobalDispatchers by lazy {
        ServiceLocator.get()
    }

    suspend fun getDirectDebitOnBoarding(): Either<DirectDebitOnBoardingDetails> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                bazaarService.getDirectDebitOnBoarding(
                    GetDirectDebitOnBoardingSingleRequest()
                ).toDirectDebitOnBoardingDetails()
            }
        }
    }

    suspend fun getCreateContractUrl(
        bankCode: String,
        nationalId: String
    ): Either<ContractCreation> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                bazaarService.getCreateContractUrl(
                    GetDirectDebitContractCreationUrlSingleRequest(
                        bankCode = bankCode,
                        nationalId = nationalId,
                        redirectUrl = DIRECT_DEBIT_ACTIVATION_REDIRECT_URL
                    )
                ).toContractCreation()
            }
        }
    }

    suspend fun getAvailableBanks(): Either<AvailableBanks> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                bazaarService.getAvailableBanks(
                    GetAvailableBanksSingleRequest()
                ).toAvailableBanks()
            }
        }
    }

    suspend fun activatePostPaid(): Either<Unit> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall<Unit> {
                bazaarService.activatePostPaid(
                    ActivatePostpaidCreditSingleRequest()
                )
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
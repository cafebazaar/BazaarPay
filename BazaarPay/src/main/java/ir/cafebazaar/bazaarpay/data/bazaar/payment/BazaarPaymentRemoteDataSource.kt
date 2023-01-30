package ir.cafebazaar.bazaarpay.data.bazaar.payment

import android.content.Context
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.payment.api.BazaarPaymentService
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

    suspend fun activatePostPaid(): Either<Unit> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall<Unit> {
                bazaarService.activatePostPaid(
                    ActivatePostpaidCreditSingleRequest()
                )
            }
        }
    }
}
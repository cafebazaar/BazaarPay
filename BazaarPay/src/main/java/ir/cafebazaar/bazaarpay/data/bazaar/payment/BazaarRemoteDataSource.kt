package ir.cafebazaar.bazaarpay.data.bazaar.payment

import android.content.Context
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.extensions.asNetworkException
import ir.cafebazaar.bazaarpay.extensions.getEitherFromResponse
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.request.ActivatePostpaidCreditSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.response.ActivatePostpaidCreditSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.request.GetAvailableBanksSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.request.GetDirectDebitContractCreationUrlSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.response.GetAvailableBanksSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.ContractCreation
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.response.GetDirectDebitContractCreationUrlSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.request.GetDirectDebitOnBoardingSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response.DirectDebitOnBoardingSingleReply
import ir.cafebazaar.bazaarpay.utils.Either
import ir.hamidbazargan.dynamicrestclient.base.Base

internal class BazaarRemoteDataSource {

    private val bazaarBase: Base = ServiceLocator.get(ServiceLocator.BAZAAR)

    fun getDirectDebitOnBoarding(): Either<DirectDebitOnBoardingDetails> {
        try {
            bazaarBase.postMethod(
                DirectDebitOnBoardingSingleReply::class.java,
                DIRECT_DEBIT_ON_BOARDING_ENDPOINT
            ).apply {
                setPostBody(GetDirectDebitOnBoardingSingleRequest())
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .singleReply
                            .getDirectDebitOnBoardingReply
                            .toDirectDebitOnBoardingDetails()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    fun getCreateContractUrl(
        bankCode: String,
        nationalId: String
    ): Either<ContractCreation> {

        try {
            bazaarBase.postMethod(
                GetDirectDebitContractCreationUrlSingleReply::class.java,
                DIRECT_DEBIT_CONTRACT_CREATION_ENDPOINT
            ).apply {
                setPostBody(
                    GetDirectDebitContractCreationUrlSingleRequest(
                        bankCode = bankCode,
                        nationalId = nationalId,
                        redirectUrl = DIRECT_DEBIT_ACTIVATION_REDIRECT_URL
                    )
                )
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .singleReply
                            .getDirectDebitContractCreationUrlReply
                            .toContractCreation()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    fun getAvailableBanks(): Either<AvailableBanks> {
        try {
            bazaarBase.postMethod(
                GetAvailableBanksSingleReply::class.java,
                DIRECT_DEBIT_AVAILABLE_BANKS_ENDPOINT
            ).apply {
                setPostBody(GetAvailableBanksSingleRequest())
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .singleReply
                            .getAvailableBanksReply
                            .toAvailableBanks()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    fun activatePostPaid(): Either<Unit> {
        try {
            bazaarBase.postMethod(
                ActivatePostpaidCreditSingleReply::class.java,
                POST_PAID_ACTIVATION_ENDPOINT
            ).apply {
                setPostBody(ActivatePostpaidCreditSingleRequest())
                getResponse().also { response ->
                    return response.getEitherFromResponse { }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    private companion object {
        const val DIRECT_DEBIT_ON_BOARDING_ENDPOINT =
            "rest-v1/process/GetDirectDebitOnBoardingRequest"
        const val DIRECT_DEBIT_AVAILABLE_BANKS_ENDPOINT = "rest-v1/process/GetAvailableBanksRequest"
        const val DIRECT_DEBIT_CONTRACT_CREATION_ENDPOINT =
            "rest-v1/process/GetDirectDebitContractCreationUrlRequest"
        const val POST_PAID_ACTIVATION_ENDPOINT = "rest-v1/process/ActivatePostpaidCreditRequest"
        var DIRECT_DEBIT_ACTIVATION_REDIRECT_URL =
            "bazaar://${
                ServiceLocator.get<Context>().packageName
            }/direct_debit_activation"
    }
}
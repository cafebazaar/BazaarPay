package ir.cafebazaar.bazaarpay.data.bazaar.payment.api

import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.request.GetAvailableBanksSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.response.GetAvailableBanksResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.request.GetDirectDebitContractCreationUrlSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.response.GetDirectDebitContractCreationUrlResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.request.GetDirectDebitOnBoardingSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response.GetDirectDebitOnBoardingResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.request.ActivatePostpaidCreditSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.response.ActivatePostpaidCreditResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

internal interface BazaarPaymentService {

    @POST("GetDirectDebitOnBoardingRequest")
    suspend fun getDirectDebitOnBoarding(
        @Body getDirectDebitOnBoardingSingleRequest: GetDirectDebitOnBoardingSingleRequest
    ): GetDirectDebitOnBoardingResponseDto

    @POST("GetDirectDebitContractCreationUrlRequest")
    suspend fun getCreateContractUrl(
        @Body getDirectDebitContractCreationUrlSingleRequest:
        GetDirectDebitContractCreationUrlSingleRequest
    ): GetDirectDebitContractCreationUrlResponseDto

    @POST("GetAvailableBanksRequest")
    suspend fun getAvailableBanks(
        @Body getAvailableBanksSingleRequest: GetAvailableBanksSingleRequest
    ): GetAvailableBanksResponseDto

    @POST("ActivatePostpaidCreditRequest")
    suspend fun activatePostPaid(
        @Body activatePostpaidCreditSingleRequest: ActivatePostpaidCreditSingleRequest
    ): ActivatePostpaidCreditResponseDto
}
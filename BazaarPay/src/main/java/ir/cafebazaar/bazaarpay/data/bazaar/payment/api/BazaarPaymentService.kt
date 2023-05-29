package ir.cafebazaar.bazaarpay.data.bazaar.payment.api

import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.request.GetAvailableBanksSingleRequestDto
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

    @POST("direct-debit/get-onboarding")
    suspend fun getDirectDebitOnBoarding(
        @Body getDirectDebitOnBoardingSingleRequest: GetDirectDebitOnBoardingSingleRequest
    ): GetDirectDebitOnBoardingResponseDto

    @POST("direct-debit/get-contract-creation-url")
    suspend fun getCreateContractUrl(
        @Body getDirectDebitContractCreationUrlSingleRequest:
        GetDirectDebitContractCreationUrlSingleRequest
    ): GetDirectDebitContractCreationUrlResponseDto

    @POST("direct-debit/get-available-banks")
    suspend fun getAvailableBanks(
        @Body getAvailableBanksSingleRequestDto: GetAvailableBanksSingleRequestDto
    ): GetAvailableBanksResponseDto

    @POST("ActivatePostpaidCreditRequest")
    suspend fun activatePostPaid(
        @Body activatePostpaidCreditSingleRequest: ActivatePostpaidCreditSingleRequest
    ): ActivatePostpaidCreditResponseDto
}
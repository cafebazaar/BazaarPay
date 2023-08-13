package ir.cafebazaar.bazaarpay.data.bazaar.payment.api

import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.response.GetAvailableBanksResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.request.GetDirectDebitContractCreationUrlSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.response.GetDirectDebitContractCreationUrlResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response.GetDirectDebitOnBoardingResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.request.ActivatePostpaidCreditSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.response.ActivatePostpaidCreditResponseDto
import ir.cafebazaar.bazaarpay.data.payment.api.PaymentService
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface BazaarPaymentService {

    @GET("pardakht/badje/v1/direct-debit/get-onboarding/")
    suspend fun getDirectDebitOnBoarding(
        @Query(PaymentService.CHECKOUT_TOKEN_LABEL) checkoutLabel: String,
    ): GetDirectDebitOnBoardingResponseDto

    @POST("pardakht/badje/v1/direct-debit/get-contract-creation-url/")
    suspend fun getCreateContractUrl(
        @Body getDirectDebitContractCreationUrlSingleRequest:
        GetDirectDebitContractCreationUrlSingleRequest
    ): GetDirectDebitContractCreationUrlResponseDto

    @GET("pardakht/badje/v1/direct-debit/get-available-banks/")
    suspend fun getAvailableBanks(
        @Query(PaymentService.CHECKOUT_TOKEN_LABEL) checkoutLabel: String,
    ): GetAvailableBanksResponseDto

    @POST("pardakht/badje/v1/ActivatePostpaidCreditRequest/")
    suspend fun activatePostPaid(
        @Body activatePostpaidCreditSingleRequest: ActivatePostpaidCreditSingleRequest
    ): ActivatePostpaidCreditResponseDto
}
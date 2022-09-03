package ir.cafebazaar.bazaarpay.data.bazaar.payment.api

import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.request.GetAvailableBanksSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.response.GetAvailableBanksSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.request.GetDirectDebitContractCreationUrlSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.response.GetDirectDebitContractCreationUrlSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.request.GetDirectDebitOnBoardingSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response.DirectDebitOnBoardingSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.request.ActivatePostpaidCreditSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.response.ActivatePostpaidCreditSingleReply
import retrofit2.http.Body
import retrofit2.http.POST

internal interface BazaarPaymentService {

    @POST("GetDirectDebitOnBoardingRequest")
    suspend fun getDirectDebitOnBoarding(
        @Body getDirectDebitOnBoardingSingleRequest: GetDirectDebitOnBoardingSingleRequest
    ): DirectDebitOnBoardingSingleReply

    @POST("GetDirectDebitContractCreationUrlRequest")
    suspend fun getCreateContractUrl(
        @Body getDirectDebitContractCreationUrlSingleRequest:
        GetDirectDebitContractCreationUrlSingleRequest
    ): GetDirectDebitContractCreationUrlSingleReply

    @POST("GetAvailableBanksRequest")
    suspend fun getAvailableBanks(
        @Body getAvailableBanksSingleRequest: GetAvailableBanksSingleRequest
    ): GetAvailableBanksSingleReply

    @POST("ActivatePostpaidCreditRequest")
    suspend fun activatePostPaid(
        @Body activatePostpaidCreditSingleRequest: ActivatePostpaidCreditSingleRequest
    ): ActivatePostpaidCreditSingleReply
}
package ir.cafebazaar.bazaarpay.data.bazaar.directdebit.api

import ir.cafebazaar.bazaarpay.data.bazaar.directdebit.banklist.response.GetAvailableBanksResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.directdebit.contractcreation.request.GetDirectDebitContractCreationUrlRequestDto
import ir.cafebazaar.bazaarpay.data.bazaar.directdebit.contractcreation.response.GetDirectDebitContractCreationUrlResponseDto
import ir.cafebazaar.bazaarpay.data.payment.api.PaymentService
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface DirectDebitService {

    @POST("direct-debit/get-contract-creation-url/")
    suspend fun getCreateContractUrl(
        @Body getDirectDebitContractCreationUrlRequestDto:
        GetDirectDebitContractCreationUrlRequestDto,
        @Query(PaymentService.PAY_ENDPOINT_LANG) lang: String
    ): GetDirectDebitContractCreationUrlResponseDto

    @GET("direct-debit/get-available-banks/")
    suspend fun getAvailableBanks(
        @Query(PaymentService.PAY_ENDPOINT_LANG) lang: String
    ): GetAvailableBanksResponseDto
}
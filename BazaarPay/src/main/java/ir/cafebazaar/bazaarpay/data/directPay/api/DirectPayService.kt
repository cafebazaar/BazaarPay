package ir.cafebazaar.bazaarpay.data.directPay.api

import ir.cafebazaar.bazaarpay.data.directPay.model.GetDirectPayContractResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface DirectPayService {

    @GET("direct-pay/contract/info")
    suspend fun getDirectPayContract(
        @Query("contract_token") contractToken: String,
    ): GetDirectPayContractResponseDto
}
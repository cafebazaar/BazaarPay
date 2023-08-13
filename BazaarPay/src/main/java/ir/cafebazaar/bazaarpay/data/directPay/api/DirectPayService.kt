package ir.cafebazaar.bazaarpay.data.directPay.api

import ir.cafebazaar.bazaarpay.data.directPay.model.FinalizeDirectPayContractRequest
import ir.cafebazaar.bazaarpay.data.directPay.model.GetDirectPayContractResponseDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface DirectPayService {

    @GET("pardakht/badje/v1/direct-pay/contract/info/")
    suspend fun getDirectPayContract(
        @Query("contract_token") contractToken: String,
    ): GetDirectPayContractResponseDto

    @POST("pardakht/badje/v1/direct-pay/contract/finalize/")
    suspend fun finalizeContract(@Body request: FinalizeDirectPayContractRequest):Response<Unit>
}
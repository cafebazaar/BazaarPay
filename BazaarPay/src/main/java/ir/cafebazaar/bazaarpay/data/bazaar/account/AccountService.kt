package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.request.GetOtpTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.response.GetOtpTokenResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.request.GetOtpTokenByCallSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.response.GetOtpTokenByCallResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.request.GetAccessTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.response.GetAccessTokenResponseDto
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.request.VerifyOtpTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.response.VerifyOtpTokenResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AccountService {

    @POST("GetOtpTokenRequest")
    suspend fun getOtpToken(
        @Body getOtpTokenSingleRequest: GetOtpTokenSingleRequest
    ): GetOtpTokenResponseDto

    @POST("GetOtpTokenByCallRequest")
    suspend fun getOtpTokenByCall(
        @Body getOtpTokenByCallSingleRequest: GetOtpTokenByCallSingleRequest
    ): GetOtpTokenByCallResponseDto

    @POST("VerifyOtpTokenRequest")
    suspend fun verifyOtpToken(
        @Body verifyOtpTokenSingleRequest: VerifyOtpTokenSingleRequest
    ): VerifyOtpTokenResponseDto

    @POST("getAccessTokenRequest")
    fun getAccessToken(
        @Body getAccessTokenSingleRequest: GetAccessTokenSingleRequest
    ): Call<GetAccessTokenResponseDto>
}
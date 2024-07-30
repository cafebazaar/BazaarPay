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

    @POST("auth/v1/get-otp-token/")
    suspend fun getOtpToken(
        @Body getOtpTokenSingleRequest: GetOtpTokenSingleRequest
    ): GetOtpTokenResponseDto

    @POST("auth/v1/get-otp-token-by-call/")
    suspend fun getOtpTokenByCall(
        @Body getOtpTokenByCallSingleRequest: GetOtpTokenByCallSingleRequest
    ): GetOtpTokenByCallResponseDto

    @POST("auth/v1/verify-otp-token/")
    suspend fun verifyOtpToken(
        @Body verifyOtpTokenSingleRequest: VerifyOtpTokenSingleRequest
    ): VerifyOtpTokenResponseDto

    @POST("auth/v1/get-access-token/")
    fun getAccessToken(
        @Body getAccessTokenSingleRequest: GetAccessTokenSingleRequest
    ): Call<GetAccessTokenResponseDto>
}
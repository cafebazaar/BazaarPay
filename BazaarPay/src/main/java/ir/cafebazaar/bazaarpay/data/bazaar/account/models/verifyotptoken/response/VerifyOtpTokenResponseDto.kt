package ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse

internal class VerifyOtpTokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
) {

    fun toLoginResponse(): LoginResponse {
        return LoginResponse(refreshToken, accessToken)
    }
}
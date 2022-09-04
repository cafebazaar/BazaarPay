package ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse

internal data class VerifyOtpTokenResponseDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("waitingSeconds") val waitingSeconds: Int
) {

    fun toLoginResponse(): LoginResponse {
        return LoginResponse(refreshToken, accessToken)
    }
}
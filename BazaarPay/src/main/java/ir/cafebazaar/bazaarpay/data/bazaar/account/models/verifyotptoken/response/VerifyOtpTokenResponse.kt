package ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.response

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse
import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse

internal class VerifyOtpTokenSingleReply(
    val singleReply: VerifyOtpTokenReply
) : BazaarBaseResponse()

internal data class VerifyOtpTokenReply(
    val verifyOtpTokenReply: VerifyOtpTokenReplyBody
)

internal data class VerifyOtpTokenReplyBody(
    val accessToken: String,
    val refreshToken: String,
    val waitingSeconds: Int
) {

    fun toLoginResponse(): LoginResponse {
        return LoginResponse(refreshToken, accessToken)
    }
}
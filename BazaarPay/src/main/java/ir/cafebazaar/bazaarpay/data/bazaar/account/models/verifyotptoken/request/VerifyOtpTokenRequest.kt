package ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.request

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class VerifyOtpTokenSingleRequest(
    username: String,
    token: String
) : BazaarBaseRequest() {

    val singleRequest: VerifyOtpTokenRequest =
        VerifyOtpTokenRequest(VerifyOtpTokenRequestBody(username, token))
}

internal class VerifyOtpTokenRequest(
    val verifyOtpTokenRequest: VerifyOtpTokenRequestBody
)

internal class VerifyOtpTokenRequestBody(
    val username: String,
    val token: String
)
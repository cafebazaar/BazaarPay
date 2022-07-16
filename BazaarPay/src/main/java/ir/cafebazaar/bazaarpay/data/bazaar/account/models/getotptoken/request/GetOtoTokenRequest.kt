package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.request

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class GetOtpTokenSingleRequest(
    username: String
) : BazaarBaseRequest() {

    val singleRequest: GetOtpTokenRequest =
        GetOtpTokenRequest(GetOtpTokenRequestBody(username))
}

internal class GetOtpTokenRequest(
    val getOtpTokenRequest : GetOtpTokenRequestBody
)

internal class GetOtpTokenRequestBody(val username: String)
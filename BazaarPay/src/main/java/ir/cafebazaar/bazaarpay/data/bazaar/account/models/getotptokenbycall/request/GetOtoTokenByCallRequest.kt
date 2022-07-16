package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.request

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class GetOtpTokenByCallSingleRequest(
    username: String
) : BazaarBaseRequest() {

    val singleRequest: GetOtpTokenByCallRequest =
        GetOtpTokenByCallRequest(GetOtpTokenByCallRequestBody(username))
}

internal class GetOtpTokenByCallRequest(
    val getOtpTokenByCallRequest : GetOtpTokenByCallRequestBody
)

internal class GetOtpTokenByCallRequestBody(val username: String)
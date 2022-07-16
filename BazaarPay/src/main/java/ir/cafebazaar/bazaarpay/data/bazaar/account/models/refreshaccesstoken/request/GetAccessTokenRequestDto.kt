package ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.request

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class GetAccessTokenSingleRequest(
    refreshToken: String
) : BazaarBaseRequest() {

    val singleRequest: GetAccessTokenRequest =
        GetAccessTokenRequest(GetAccessTokenRequestBody(refreshToken))
}

internal class GetAccessTokenRequest(
    val getAccessTokenRequest : GetAccessTokenRequestBody
)

internal class GetAccessTokenRequestBody(val refreshToken: String)
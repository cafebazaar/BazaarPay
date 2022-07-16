package ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.response

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse

internal class GetAccessTokenSingleReply(
    val singleReply: GetAccessReply
) : BazaarBaseResponse()

internal data class GetAccessReply(
    val getAccessTokenReply: GetAccessTokenReplyBody
)

internal data class GetAccessTokenReplyBody(
    val accessToken: String
) {

    fun getToken(): String {
        return accessToken
    }
}
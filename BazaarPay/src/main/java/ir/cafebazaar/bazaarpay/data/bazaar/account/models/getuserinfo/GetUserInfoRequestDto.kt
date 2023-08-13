package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getuserinfo

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class GetUserInfoSingleRequest : BazaarBaseRequest() {

    val singleRequest: GetUserInfoRequest =
        GetUserInfoRequest(GetUserInfoRequestBody())
}

internal class GetUserInfoRequest(
    val getUserInfoRequest: GetUserInfoRequestBody
)

internal class GetUserInfoRequestBody()
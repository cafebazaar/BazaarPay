package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getuserinfo.GetUserInfoSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo.GetUserInfoReplyDto
import retrofit2.http.Body
import retrofit2.http.POST

internal interface UserInfoService {

    @POST("GetUserInfoRequest")
    suspend fun getUserInfoRequest(
        @Body getUserInfoSingleRequest: GetUserInfoSingleRequest
    ): GetUserInfoReplyDto
}
package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo.AutoLoginUserInfoReplyDto
import retrofit2.http.GET
import retrofit2.http.POST

internal interface UserInfoService {

    @GET("pardakht/badje/v1/user/info//")
    suspend fun getUserInfoRequest(): AutoLoginUserInfoReplyDto
}
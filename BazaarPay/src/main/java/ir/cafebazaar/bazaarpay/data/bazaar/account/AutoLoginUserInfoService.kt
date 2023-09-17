package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo.AutoLoginUserInfoReplyDto
import retrofit2.http.GET

internal interface AutoLoginUserInfoService {

    @GET("pardakht/badje/v1/get-user-info/")
    suspend fun getUserInfoRequest(): AutoLoginUserInfoReplyDto
}
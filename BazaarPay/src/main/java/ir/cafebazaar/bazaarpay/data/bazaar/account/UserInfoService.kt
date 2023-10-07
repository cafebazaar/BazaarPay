package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo.AutoLoginUserInfoReplyDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface UserInfoService {

    @GET("pardakht/badje/v1/user/info/")
    suspend fun getUserInfoRequest(
        @Query(CHECKOUT_TOKEN_LABEL) checkoutLabel: String?
    ): AutoLoginUserInfoReplyDto


    companion object {
        const val CHECKOUT_TOKEN_LABEL = "checkout_token"
    }
}
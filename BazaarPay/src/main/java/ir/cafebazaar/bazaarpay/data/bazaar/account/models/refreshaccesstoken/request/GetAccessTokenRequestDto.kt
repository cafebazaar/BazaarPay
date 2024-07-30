package ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.request

import com.google.gson.annotations.SerializedName

internal data class GetAccessTokenSingleRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)
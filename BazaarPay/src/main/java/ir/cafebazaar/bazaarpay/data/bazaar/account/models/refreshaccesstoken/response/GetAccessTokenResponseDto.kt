package ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.response

import com.google.gson.annotations.SerializedName

internal class GetAccessTokenResponseDto(
    @SerializedName("access_token") val accessToken: String
)
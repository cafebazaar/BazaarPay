package ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.response

import com.google.gson.annotations.SerializedName

internal data class GetAccessTokenResponseDto(
    @SerializedName("accessToken") val accessToken: String
)
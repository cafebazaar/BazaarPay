package ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo

import com.google.gson.annotations.SerializedName

internal class GetUserInfoReplyDto(
    @SerializedName("accountID") val accountID: String,
)
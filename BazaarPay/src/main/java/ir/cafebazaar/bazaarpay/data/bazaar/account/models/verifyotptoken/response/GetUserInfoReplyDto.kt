package ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.response

import com.google.gson.annotations.SerializedName

internal class GetUserInfoReplyDto(
    @SerializedName("accountID") val accountID: String,
)
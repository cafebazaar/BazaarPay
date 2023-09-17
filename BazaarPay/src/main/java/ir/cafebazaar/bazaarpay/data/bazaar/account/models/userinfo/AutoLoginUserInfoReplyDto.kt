package ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo

import com.google.gson.annotations.SerializedName

internal class AutoLoginUserInfoReplyDto(
    @SerializedName("account_id") val accountID: String,
    @SerializedName("phone_number") val phoneNumber: String,
)

internal fun AutoLoginUserInfoReplyDto.toAutoLoginUserInfo(): AutoLoginUserInfo {
    return AutoLoginUserInfo(accountID, phoneNumber)
}
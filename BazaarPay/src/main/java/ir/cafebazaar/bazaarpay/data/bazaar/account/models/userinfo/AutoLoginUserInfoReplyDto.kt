package ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo

import com.google.gson.annotations.SerializedName

internal class AutoLoginUserInfoReplyDto(
    @SerializedName("phone_number") val phoneNumber: String,
)

internal fun AutoLoginUserInfoReplyDto.toAutoLoginUserInfo(): UserInfo {
    return UserInfo(phoneNumber)
}
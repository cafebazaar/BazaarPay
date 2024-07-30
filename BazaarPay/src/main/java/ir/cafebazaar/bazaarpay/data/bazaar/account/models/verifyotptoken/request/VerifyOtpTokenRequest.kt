package ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.request

import com.google.gson.annotations.SerializedName

internal data class VerifyOtpTokenSingleRequest(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("token")
    val token: String
)
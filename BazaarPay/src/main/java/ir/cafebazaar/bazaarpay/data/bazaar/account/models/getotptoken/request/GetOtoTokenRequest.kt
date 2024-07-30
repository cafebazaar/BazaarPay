package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.request

import com.google.gson.annotations.SerializedName

data class GetOtpTokenSingleRequest(
    @SerializedName("phone")
    val username: String
)
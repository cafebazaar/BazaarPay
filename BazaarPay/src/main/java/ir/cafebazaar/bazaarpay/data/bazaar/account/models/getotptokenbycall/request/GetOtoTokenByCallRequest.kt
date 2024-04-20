package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.request

import com.google.gson.annotations.SerializedName

internal data class GetOtpTokenByCallSingleRequest(
    @SerializedName("phone")
    val phone: String
)
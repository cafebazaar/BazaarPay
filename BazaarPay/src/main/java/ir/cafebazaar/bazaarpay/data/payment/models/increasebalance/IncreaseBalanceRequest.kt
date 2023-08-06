package ir.cafebazaar.bazaarpay.data.payment.models.increasebalance

import com.google.gson.annotations.SerializedName

internal data class IncreaseBalanceRequest(
    @SerializedName("amount") val amount: Long,
    @SerializedName("redirect_url") val redirectUrl: String
)
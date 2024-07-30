package ir.cafebazaar.bazaarpay.data.payment.models.pay.request

import com.google.gson.annotations.SerializedName

internal data class PayRequest(
    @SerializedName("checkout_token") val checkoutToken: String,
    @SerializedName("method") val method: String,
    @SerializedName("amount") val amount: Long?,
    @SerializedName("redirect_url") val redirectUrl: String,
    @SerializedName("accessibility") val accessibility: Boolean,
)
package ir.cafebazaar.bazaarpay.data.payment.models.pay.request

import com.google.gson.annotations.SerializedName

internal data class TraceRequest(
    @SerializedName("checkout_token") val checkoutToken: String
)
package ir.cafebazaar.bazaarpay.data.payment.models.pay.request

import com.google.gson.annotations.SerializedName

internal data class CommitRequest(
    @SerializedName("checkout_token") val checkoutToken: String
)
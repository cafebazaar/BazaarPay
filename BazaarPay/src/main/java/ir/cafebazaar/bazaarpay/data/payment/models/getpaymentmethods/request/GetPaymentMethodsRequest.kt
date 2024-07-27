package ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.request

import com.google.gson.annotations.SerializedName

internal data class GetPaymentMethodsRequest(
    @SerializedName("checkout_token") val checkoutToken: String,
    @SerializedName("accessibility") val accessibility: Boolean,
)
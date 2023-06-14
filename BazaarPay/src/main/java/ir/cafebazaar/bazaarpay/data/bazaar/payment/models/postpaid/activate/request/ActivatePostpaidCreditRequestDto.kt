package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.request

import com.google.gson.annotations.SerializedName

internal data class ActivatePostpaidCreditSingleRequest(
    @SerializedName("checkout_token") val checkoutToken: String
)
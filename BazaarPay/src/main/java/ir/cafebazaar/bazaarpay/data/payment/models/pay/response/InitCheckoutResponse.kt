package ir.cafebazaar.bazaarpay.data.payment.models.pay.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.payment.models.PaymentBaseResponse
import ir.cafebazaar.bazaarpay.data.payment.models.pay.InitCheckoutResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult

internal data class InitCheckoutResponse(
    @SerializedName("checkout_token") val checkoutToken: String,
    @SerializedName("payment_url") val paymentUrl: String
): PaymentBaseResponse() {

    fun toInitCheckoutResult(): InitCheckoutResult {
        return InitCheckoutResult(checkoutToken, paymentUrl)
    }
}
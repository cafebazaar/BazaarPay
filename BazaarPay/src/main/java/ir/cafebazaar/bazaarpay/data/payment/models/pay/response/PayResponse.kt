package ir.cafebazaar.bazaarpay.data.payment.models.pay.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.payment.models.PaymentBaseResponse
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult

internal data class PayResponse(
    @SerializedName("redirect_url") val redirectUrl: String
): PaymentBaseResponse() {

    fun toPayResult(): PayResult {
        return PayResult(redirectUrl)
    }
}
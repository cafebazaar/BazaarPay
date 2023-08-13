package ir.cafebazaar.bazaarpay.data.payment.models.pay.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.payment.models.PaymentBaseResponse
import ir.cafebazaar.bazaarpay.data.payment.models.pay.BalanceResult

internal data class BalanceResponseDto(
    @SerializedName("balance") val amount: String,
    @SerializedName("balance_string") val humanReadableAmount: String
) : PaymentBaseResponse() {

    fun toBalanceResult(): BalanceResult {
        return BalanceResult(amount, humanReadableAmount)
    }
}
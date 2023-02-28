package ir.cafebazaar.bazaarpay.data.payment.models.pay.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.payment.models.PaymentBaseResponse
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PurchaseStatus

internal data class TraceResponse(
    @SerializedName("status") val status: String
): PaymentBaseResponse() {

    fun toPurchaseState(): PurchaseStatus {
       return when (status) {
           "invalid_token" -> PurchaseStatus.InvalidToken
           "unpaid" -> PurchaseStatus.UnPaid
           "paid_not_committed" -> PurchaseStatus.PaidNotCommitted
           "paid_committed" -> PurchaseStatus.PaidCommitted
           "refunded" -> PurchaseStatus.Refunded
           "timed_out" -> PurchaseStatus.TimedOut
           "paid_not_committed_refunded" -> PurchaseStatus.PaidNotCommittedRefunded
           else -> {
               PurchaseStatus.ApiError
           }
       }
    }
}
package ir.cafebazaar.bazaarpay.data.analytics.model

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.analytics.model.EventType

internal data class ActionLogDto(
    @SerializedName("id") val id: Long,
    @SerializedName("source") val source: String,
    @SerializedName("type") val type: EventType,
    @SerializedName("trace_id") val traceId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("account_id") val accountId: String,
    @SerializedName("device_id") val deviceId: String?,
    @SerializedName("where") val where: String?,
    @SerializedName("what") val what: String?,
    @SerializedName("extra") val extra: String?,
    @SerializedName("payment_flow_details") val paymentFlowDetailsDto: PaymentFlowDetailsDto,
)

internal data class PaymentFlowDetailsDto(
    @SerializedName("checkout_id") val checkOutToken: String?,
    @SerializedName("merchant_name") val merchantName: String?,
    @SerializedName("amount") val amount: String?
)

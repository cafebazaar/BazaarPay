package ir.cafebazaar.bazaarpay.analytics.model

import com.google.gson.annotations.SerializedName

internal data class ActionLogRequestDto(
    @SerializedName("source") val source: Int,
    @SerializedName("type") val type: EventType,
    @SerializedName("source") val traceId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("account_id") val accountId: String,
    @SerializedName("device_id") val deviceId: String?,
    @SerializedName("where") val where: String?,
    @SerializedName("extra") val extra: String?,
)

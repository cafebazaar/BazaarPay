package ir.cafebazaar.bazaarpay.data.payment.models.pay.request

import com.google.gson.annotations.SerializedName

internal data class InitCheckoutRequest(
    @SerializedName("amount") val amount: Long,
    @SerializedName("destination") val destination: String,
    @SerializedName("service_name") val serviceName: String,
)
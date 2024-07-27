package ir.cafebazaar.bazaarpay.data.payment.models.increasebalance

import com.google.gson.annotations.SerializedName

internal data class GetIncreaseBalanceOptionsRequest(
    @SerializedName("accessibility") val accessibility: Boolean,
)
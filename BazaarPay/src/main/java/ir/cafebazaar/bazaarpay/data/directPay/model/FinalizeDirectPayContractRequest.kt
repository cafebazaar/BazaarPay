package ir.cafebazaar.bazaarpay.data.directPay.model

import com.google.gson.annotations.SerializedName

internal data class FinalizeDirectPayContractRequest(
    @SerializedName("contract_token")
    val contractToken: String,
    @SerializedName("action")
    val action: String,
)
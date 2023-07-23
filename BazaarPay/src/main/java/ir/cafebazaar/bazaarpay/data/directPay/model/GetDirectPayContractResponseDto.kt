package ir.cafebazaar.bazaarpay.data.directPay.model

import com.google.gson.annotations.SerializedName

internal class GetDirectPayContractResponseDto(
    @SerializedName("description") val description: String,
    @SerializedName("merchant_name") val merchantName: String,
    @SerializedName("merchant_logo") val merchantLogo: String,
    @SerializedName("status") val status: String,
    @SerializedName("status_message") val statusMessage: String,
) {

    fun toDirectPayContract(): DirectPayContractResponse {
        return DirectPayContractResponse(
            description = description,
            merchantName = merchantName,
            merchantLogo = merchantLogo,
            status = status,
            statusMessage = statusMessage
        )
    }
}
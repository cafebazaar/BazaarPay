package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.ContractCreation

internal data class GetDirectDebitContractCreationUrlResponseDto(
    @SerializedName("url") val url: String
) {

    fun toContractCreation(): ContractCreation {
        return ContractCreation(url)
    }
}
package ir.cafebazaar.bazaarpay.data.bazaar.directdebit.contractcreation.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.directdebit.contractcreation.ContractCreation

internal class GetDirectDebitContractCreationUrlResponseDto(
    @SerializedName("url") val url: String
) {

    fun toContractCreation(): ContractCreation {
        return ContractCreation(url)
    }
}
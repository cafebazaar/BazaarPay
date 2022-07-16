package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.response

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.ContractCreation

internal class GetDirectDebitContractCreationUrlSingleReply(
    val singleReply: GetDirectDebitContractCreationUrlReply
) : BazaarBaseResponse()

internal data class GetDirectDebitContractCreationUrlReply(
    val getDirectDebitContractCreationUrlReply: GetDirectDebitContractCreationUrlReplyBody
)

internal data class GetDirectDebitContractCreationUrlReplyBody(
    val url: String
) {

    fun toContractCreation(): ContractCreation {
        return ContractCreation(url)
    }
}
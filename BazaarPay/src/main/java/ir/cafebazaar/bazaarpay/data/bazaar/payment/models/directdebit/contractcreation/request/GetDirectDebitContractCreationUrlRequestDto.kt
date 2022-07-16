package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.request

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class GetDirectDebitContractCreationUrlSingleRequest(
    bankCode: String,
    nationalId: String,
    redirectUrl: String
) : BazaarBaseRequest() {

    val singleRequest: GetDirectDebitContractCreationUrlRequest =
        GetDirectDebitContractCreationUrlRequest(
            GetDirectDebitContractCreationUrlRequestBody(
                bankCode,
                nationalId,
                redirectUrl
            )
        )
}

internal class GetDirectDebitContractCreationUrlRequest(
    val getDirectDebitContractCreationUrlRequest: GetDirectDebitContractCreationUrlRequestBody
)

internal data class GetDirectDebitContractCreationUrlRequestBody(
    var bankCode: String,
    var nationalId: String,
    var redirectUrl: String,
    var source: Int = 0
)
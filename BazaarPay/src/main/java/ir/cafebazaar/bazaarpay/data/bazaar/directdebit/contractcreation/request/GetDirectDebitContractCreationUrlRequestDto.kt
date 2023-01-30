package ir.cafebazaar.bazaarpay.data.bazaar.directdebit.contractcreation.request

internal data class GetDirectDebitContractCreationUrlRequestDto(
    var bankCode: String,
    var nationalId: String,
    var redirectUrl: String,
    var source: Int = 0
)
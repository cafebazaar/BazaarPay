package ir.cafebazaar.bazaarpay.data.directPay.model

internal class DirectPayContractResponse(
    val description: String,
    val merchantName: String,
    val merchantLogo: String,
    val status: String,
    val statusMessage: String,
)
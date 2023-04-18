package ir.cafebazaar.bazaarpay.data.payment.models.pay

data class InitCheckoutResult(
    val checkoutToken: String,
    val paymentUrl: String
)
package ir.cafebazaar.bazaarpay.screens.payment.paymentmethods

import androidx.annotation.StringRes

internal class PaymentMethodViewLoader(
    val price: String?,
    @StringRes val payButton: Int,
    val subDescription: String?,
    val enabled: Boolean = true
)
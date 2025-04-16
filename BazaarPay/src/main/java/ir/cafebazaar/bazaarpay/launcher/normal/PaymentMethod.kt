package ir.cafebazaar.bazaarpay.launcher.normal

import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType

enum class PaymentMethod(internal val value: String) {
    INCREASE_BALANCE(value = PaymentMethodsType.INCREASE_BALANCE.value),
}
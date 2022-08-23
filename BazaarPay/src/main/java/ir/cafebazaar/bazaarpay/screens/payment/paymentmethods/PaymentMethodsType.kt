package ir.cafebazaar.bazaarpay.screens.payment.paymentmethods

internal enum class PaymentMethodsType(val value: String) {
    INCREASE_BALANCE("increase_balance"),
    ENOUGH_BALANCE("enough_balance"),
    DIRECT_DEBIT_ACTIVATION("direct_debit_activation"),
    DIRECT_DEBIT("direct_debit"),
    POSTPAID_CREDIT_ACTIVATION("postpaid_credit_activation"),
    POSTPAID_CREDIT("postpaid_credit");
}
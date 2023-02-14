package ir.cafebazaar.bazaarpay.data.payment.models.pay

enum class PurchaseStatus {
    InvalidToken,
    UnPaid,
    PaidNotCommitted,
    PaidCommitted,
    Refunded,
    TimedOut,
    UnKnown
}
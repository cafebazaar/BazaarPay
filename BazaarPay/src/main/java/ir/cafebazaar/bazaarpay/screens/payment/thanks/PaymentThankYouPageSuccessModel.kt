package ir.cafebazaar.bazaarpay.screens.payment.thanks

import androidx.annotation.StringRes

internal class PaymentThankYouPageSuccessModel(
    val paymentProgressBarModel: PaymentProgressBarModel,
    val messageTextModel: PaymentThankYouPageSuccessMessageModel
)

internal class PaymentProgressBarModel(
    val successMessageCountDown: Long,
    val seconds: Int
)

internal class PaymentThankYouPageSuccessMessageModel(
    val argMessage: String?,
    @StringRes val defaultMessageId: Int
)
package ir.cafebazaar.bazaarpay.screens.payment.thanks

import androidx.annotation.StringRes

internal class PaymentThankYouPageSuccessModel(
    val successButtonTextModel: SuccessButtonTextModel,
    val messageTextModel: PaymentThankYouPageSuccessMessageModel
)

internal class SuccessButtonTextModel(
    @StringRes val successButtonTextId: Int,
    val successMessageCountDown: Long
)

internal class PaymentThankYouPageSuccessMessageModel(
    val argMessage: String?,
    @StringRes val defaultMessageId: Int
)
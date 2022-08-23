package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.response

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse

internal class ActivatePostpaidCreditSingleReply(
    val singleReply: ActivatePostpaidCreditReply
) : BazaarBaseResponse()

internal data class ActivatePostpaidCreditReply(
    val activatePostpaidCreditReply: ActivatePostpaidCreditReplyBody
)

internal object ActivatePostpaidCreditReplyBody
package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.response

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.WaitingTime
import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse

internal class GetOtpTokenByCallSingleReply(
    val singleReply: GetOtpTokenByCallReply
) : BazaarBaseResponse()

internal data class GetOtpTokenByCallReply(
    val getOtpTokenReply: GetOtpTokenByCallReplyBody
)

internal data class GetOtpTokenByCallReplyBody(
    val waitingSeconds: Long
) {

    fun toWaitingTime(): WaitingTime {
        return WaitingTime(waitingSeconds)
    }
}
package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.response

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse

internal class GetOtpTokenSingleReply(
    val singleReply: GetOtpTokenReply
) : BazaarBaseResponse()

internal data class GetOtpTokenReply(
    val getOtpTokenReply: GetOtpTokenReplyBody
)

internal data class GetOtpTokenReplyBody(
    val waitingSeconds: Long,
    val callIsEnabled: Boolean
) {

    fun toWaitingTimeWithEnableCall(): WaitingTimeWithEnableCall {
        return WaitingTimeWithEnableCall(waitingSeconds, callIsEnabled)
    }
}
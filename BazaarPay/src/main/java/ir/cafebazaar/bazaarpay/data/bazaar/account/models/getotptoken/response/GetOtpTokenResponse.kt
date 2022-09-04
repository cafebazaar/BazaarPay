package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.response

import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse

internal data class GetOtpTokenDto(
    val waitingSeconds: Long,
    val callIsEnabled: Boolean
) {

    fun toWaitingTimeWithEnableCall(): WaitingTimeWithEnableCall {
        return WaitingTimeWithEnableCall(waitingSeconds, callIsEnabled)
    }
}
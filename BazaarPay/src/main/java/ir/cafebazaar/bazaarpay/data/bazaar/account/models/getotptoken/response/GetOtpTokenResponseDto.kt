package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall

internal class GetOtpTokenResponseDto(
    @SerializedName("waiting_seconds") val waitingSeconds: Long,
    @SerializedName("call_is_enabled") val callIsEnabled: Boolean
) {

    fun toWaitingTimeWithEnableCall(): WaitingTimeWithEnableCall {
        return WaitingTimeWithEnableCall(waitingSeconds, callIsEnabled)
    }
}
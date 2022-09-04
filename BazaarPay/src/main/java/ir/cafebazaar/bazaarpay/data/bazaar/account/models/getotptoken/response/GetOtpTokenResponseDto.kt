package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall

internal data class GetOtpTokenResponseDto(
    @SerializedName("waitingSeconds") val waitingSeconds: Long,
    @SerializedName("callIsEnabled") val callIsEnabled: Boolean
) {

    fun toWaitingTimeWithEnableCall(): WaitingTimeWithEnableCall {
        return WaitingTimeWithEnableCall(waitingSeconds, callIsEnabled)
    }
}
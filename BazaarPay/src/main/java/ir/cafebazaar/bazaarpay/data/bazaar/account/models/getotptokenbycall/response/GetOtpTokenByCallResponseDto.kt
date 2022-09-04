package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.WaitingTime

internal data class GetOtpTokenByCallResponseDto(
    @SerializedName("waitingSeconds") val waitingSeconds: Long
) {

    fun toWaitingTime(): WaitingTime {
        return WaitingTime(waitingSeconds)
    }
}
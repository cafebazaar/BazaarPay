package ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WaitingTimeWithEnableCall(val seconds: Long, val isCallEnabled: Boolean): Parcelable
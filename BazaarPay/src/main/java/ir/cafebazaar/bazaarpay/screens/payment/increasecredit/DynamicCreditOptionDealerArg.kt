package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicCreditOptionDealerArg(
    val iconUrl: String?,
    val name: String?,
    val info: String?,
    val priceString: String
) : Parcelable
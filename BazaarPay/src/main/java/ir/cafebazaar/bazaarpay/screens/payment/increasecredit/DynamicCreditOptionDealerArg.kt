package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class DynamicCreditOptionDealerArg: Parcelable {

    @Parcelize
    data class DynamicCreditWithMerchantArg(
        val iconUrl: String?,
        val name: String?,
        val info: String?,
        val priceString: String
    ) : DynamicCreditOptionDealerArg()

    @Parcelize
    object DirectDynamicCreditArg : DynamicCreditOptionDealerArg()
}
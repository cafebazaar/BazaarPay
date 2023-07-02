package ir.cafebazaar.bazaarpay.arg

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal sealed class BazaarPayActivityArgs : Parcelable {

    data class Normal(
        val checkoutToken: String,
        val phoneNumber: String? = null,
        val isDarkMode: Boolean? = null,
        val autoLoginPhoneNumber: String? = null,
        val isAutoLoginEnable: Boolean = false,
    ) : BazaarPayActivityArgs()

    data class DirectPayContract(
        val phoneNumber: String? = null,
        val contractToken: String
    ) : BazaarPayActivityArgs()
}

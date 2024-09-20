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
        val authToken: String? = null,
        val isAccessibilityEnable: Boolean = false,
        val isInternalWebPageEnable: Boolean = true,
    ) : BazaarPayActivityArgs()

    data class DirectPayContract(
        val contractToken: String,
        val phoneNumber: String? = null,
        val message: String? = null,
        val authToken: String? = null,
    ) : BazaarPayActivityArgs()

    data class Login(val phoneNumber: String? = null) : BazaarPayActivityArgs()

    data class IncreaseBalance(val authToken: String? = null) : BazaarPayActivityArgs()
}

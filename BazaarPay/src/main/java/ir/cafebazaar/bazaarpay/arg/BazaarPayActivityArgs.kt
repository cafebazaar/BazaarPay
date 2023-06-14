package ir.cafebazaar.bazaarpay.arg

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class BazaarPayActivityArgs(
    val checkoutToken: String,
    val phoneNumber: String? = null,
    val isDarkMode: Boolean? = null,
    val autoLoginPhoneNumber: String? = null,
    val isAutoLoginEnable: Boolean = false,
) : Parcelable

package ir.cafebazaar.bazaarpay.arg

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class BazaarPayActivityArgs(
    val checkoutToken: String,
    val phoneNumber: String? = null,
    val isDarkMode: Boolean = false,
    val language: String,
    val languageNumber: Int
) : Parcelable

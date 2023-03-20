package ir.cafebazaar.bazaarpay

data class BazaarPayOptions(
    val checkoutToken: String,
    val isEnglish: Boolean = false,
    val isInDarkMode: Boolean = false,
    val phoneNumber: String? = null
)

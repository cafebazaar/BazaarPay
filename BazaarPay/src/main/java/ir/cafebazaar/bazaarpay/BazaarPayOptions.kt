package ir.cafebazaar.bazaarpay

/**
 * The mandatory and optional parameters to configure the payment flow.
 *
 * @property checkoutToken the unique identifier that provides essential payment information.
 * @property isEnglish forces the English language for the payment flow. The default value is `false`, and the Persian language will be used.
 * @property isInDarkMode enables *Dark Mode* for the UI elements of the payment flow, which are in *Light Mode* by default.
 * @property phoneNumber the default phone number to pre-fill the login screen's input field. It uses a `null` value by default, resulting in no pre-filled input.
 */
data class BazaarPayOptions(
    val checkoutToken: String,
    val isEnglish: Boolean = false,
    val isInDarkMode: Boolean = false,
    val phoneNumber: String? = null
)

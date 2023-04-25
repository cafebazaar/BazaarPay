package ir.cafebazaar.bazaarpay

/**
 * The mandatory and optional parameters to configure the payment flow.
 *
 * @property checkoutToken the unique identifier of a checkout process.
 * @property isInDarkMode enables *Dark Mode* for the UI elements of the payment flow, which are in *Light Mode* by default.
 * @property phoneNumber the default phone number to pre-fill the login screen's input field. It uses a `null` value by default, resulting in no pre-filled input.
 */
class BazaarPayOptions(
    val checkoutToken: String,
    val isInDarkMode: Boolean = false,
    val phoneNumber: String? = null
)

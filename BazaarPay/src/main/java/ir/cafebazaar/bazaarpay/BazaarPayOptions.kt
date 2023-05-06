package ir.cafebazaar.bazaarpay

/**
 * The mandatory and optional parameters to configure the payment flow.
 *
 * @property checkoutToken the unique identifier that provides essential payment information.
 * @property isInDarkMode enables *Dark Mode* for the UI elements of the payment flow, which are in *Light Mode* by default.
 * @property phoneNumber the default phone number to pre-fill the login screen's input field. It uses a `null` value by default, resulting in no pre-filled input.
 */
class BazaarPayOptions @Deprecated(
    "isInDarkMode is deprecated, BazaarPay follow your application theme, use the ",
    replaceWith = ReplaceWith("BazaarPayOptions(checkoutToken,phoneNumber)"),
    level = DeprecationLevel.WARNING
) constructor(
    val checkoutToken: String,
    val isInDarkMode: Boolean? = null,
    val phoneNumber: String? = null
) {

    constructor(
        checkoutToken: String,
        phoneNumber: String? = null
    ) : this(checkoutToken = checkoutToken, isInDarkMode = null, phoneNumber = phoneNumber)
}

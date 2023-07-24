package ir.cafebazaar.bazaarpay.login

/**
 * The mandatory and optional parameters to configure the payment flow.
 *
 * @property phoneNumber the default phone number to pre-fill the login screen's input field. It uses a `null` value by default, resulting in no pre-filled input.
 */
data class BazaarPayLoginOptions(
    val phoneNumber: String? = null
)
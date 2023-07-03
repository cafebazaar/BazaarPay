package ir.cafebazaar.bazaarpay.directPay

/**
 * The mandatory and optional parameters to configure the payment flow.
 *
 * @property contractToken the unique identifier that received from init-contract API
 * @property phoneNumber the default phone number to pre-fill the login screen's input field. It uses a `null` value by default, resulting in no pre-filled input.
 */
data class DirectPayContractOptions(
    val contractToken: String,
    val phoneNumber: String? = null,
)
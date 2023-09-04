package ir.cafebazaar.bazaarpay.launcher.increasebalance

/**
 * The mandatory and optional parameters to configure the payment flow.
 *
 * @property authToken Optional token for autoLogin
 */
data class IncreaseBalanceOptions(
    val authToken: String? = null,
)
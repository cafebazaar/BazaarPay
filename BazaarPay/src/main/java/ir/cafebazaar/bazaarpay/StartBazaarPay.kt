package ir.cafebazaar.bazaarpay

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs
import kotlinx.coroutines.flow.flow

/**
 * An [ActivityResultContract] to start a payment.
 *
 * The input is a [BazaarPayOptions] that configures the payment flow.
 *
 * Returns `true` if the payment was successful.
 *
 * Example of usage:
 *
 * ```
 * val bazaarPayLauncher = registerForActivityResult(StartBazaarPay()) { isSuccessful ->
 *     if (isSuccessful) {
 *          // A successful payment.
 *     } else {
 *          // An unsuccessful payment (Canceled by the user).
 *     }
 * }
 *
 * val options = BazaarPayOptions(checkoutToken = "CHECKOUT_TOKEN")
 * bazaarPayLauncher.launch(options)
 */
class StartBazaarPay : ActivityResultContract<BazaarPayOptions, Boolean>() {

    override fun createIntent(context: Context, input: BazaarPayOptions): Intent {
        ServiceLocator.initializeConfigs(
            input.checkoutToken,
            input.phoneNumber,
            input.isInDarkMode
        )
        val bazaarPayActivityArgs = BazaarPayActivityArgs(
            input.checkoutToken,
            input.phoneNumber,
            input.isInDarkMode
        )
        return Intent(context, BazaarPayActivity::class.java).apply {
            putExtra(BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}

package ir.cafebazaar.bazaarpay.launcher.normal

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs
import ir.cafebazaar.bazaarpay.main.BazaarPayActivity

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
        ServiceLocator.initializeConfigsForNormal(
            checkoutToken = input.checkoutToken,
            phoneNumber = input.phoneNumber,
            isAutoLoginEnable = input.isAutoLoginEnable,
            autoLoginPhoneNumber = input.autoLoginPhoneNumber,
            autoLoginAuthToken = input.authToken,
            isAccessibilityEnable = input.isAccessibilityEnable,
        )
        val bazaarPayActivityArgs = BazaarPayActivityArgs.Normal(
            checkoutToken = input.checkoutToken,
            phoneNumber = input.phoneNumber,
            isDarkMode = input.isInDarkMode,
            autoLoginPhoneNumber = input.autoLoginPhoneNumber,
            isAutoLoginEnable = input.isAutoLoginEnable,
            authToken = input.authToken,
            isAccessibilityEnable = input.isAccessibilityEnable,
            paymentMethod = input.paymentMethod?.value,
        )
        return Intent(context, BazaarPayActivity::class.java).apply {
            putExtra(BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}

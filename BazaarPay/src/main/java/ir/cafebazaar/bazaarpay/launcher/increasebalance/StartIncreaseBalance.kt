package ir.cafebazaar.bazaarpay.launcher.increasebalance

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ir.cafebazaar.bazaarpay.ServiceLocator.initializeShareConfigs
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs
import ir.cafebazaar.bazaarpay.main.BazaarPayActivity

/**
 * An [ActivityResultContract] to start Login and get the result.
 *
 * Returns `true` if the login was successful.
 *
 * Example of usage:
 *
 * ```
 * val bazaarPayIncreaseBalanceLauncher = registerForActivityResult(StartIncreaseBalance()) { isSuccessful ->
 *     if (isSuccessful) {
 *          // A successful increase balance.
 *     } else {
 *          // An unsuccessful increase balance.
 *     }
 * }
 *
 * bazaarPayIncreaseBalanceLauncher.launch(options)
 */
class StartIncreaseBalance : ActivityResultContract<IncreaseBalanceOptions, Boolean>() {

    override fun createIntent(context: Context, input: IncreaseBalanceOptions): Intent {
        initializeShareConfigs()
        val bazaarPayActivityArgs = BazaarPayActivityArgs.IncreaseBalance(authToken = input.authToken)
        return Intent(context, BazaarPayActivity::class.java).apply {
            putExtra(BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}

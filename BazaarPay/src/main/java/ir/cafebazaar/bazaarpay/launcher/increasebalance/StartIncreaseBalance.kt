package ir.cafebazaar.bazaarpay.launcher.increasebalance

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ir.cafebazaar.bazaarpay.BazaarPayActivity
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs

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
class StartIncreaseBalance : ActivityResultContract<Unit, Boolean>() {

    override fun createIntent(context: Context, input: Unit): Intent {

        val bazaarPayActivityArgs = BazaarPayActivityArgs.IncreaseBalance
        return Intent(context, BazaarPayActivity::class.java).apply {
            putExtra(BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}

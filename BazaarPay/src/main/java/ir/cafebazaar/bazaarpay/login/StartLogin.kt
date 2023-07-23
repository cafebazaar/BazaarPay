package ir.cafebazaar.bazaarpay.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ir.cafebazaar.bazaarpay.BazaarPayActivity
import ir.cafebazaar.bazaarpay.ServiceLocator.initializeConfigsForLogin
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs

/**
 * An [ActivityResultContract] to start Login and get the result.
 *
 * The input is a [LoginOptions] that configures the payment flow.
 *
 * Returns `true` if the login was successful.
 *
 * Example of usage:
 *
 * ```
 * val bazaarPayLoginLauncher = registerForActivityResult(StartLogin()) { isSuccessful ->
 *     if (isSuccessful) {
 *          // A successful login.
 *     } else {
 *          // An unsuccessful login.
 *     }
 * }
 *
 * val options = LoginOptions(phoneNumber = "PHONE_NUMBER")
 * bazaarPayLoginLauncher.launch(options)
 */
class StartLogin : ActivityResultContract<LoginOptions, Boolean>() {

    override fun createIntent(context: Context, input: LoginOptions): Intent {
        initializeConfigsForLogin(
            phoneNumber = input.phoneNumber,
        )
        val bazaarPayActivityArgs = BazaarPayActivityArgs.LoginContract(
            phoneNumber = input.phoneNumber,
        )
        return Intent(context, BazaarPayActivity::class.java).apply {
            putExtra(BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}

package ir.cafebazaar.bazaarpay

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs

@Deprecated(
    message = "Use the BazaarPayContract class instead",
    level = DeprecationLevel.WARNING
)
object BazaarPayLauncher {

    fun launchBazaarPay(
        context: Context,
        checkoutToken: String,
        phoneNumber: String? = null,
        isDarkMode: Boolean = false,
        activityResultLauncher: ActivityResultLauncher<Intent>
    ) {
        ServiceLocator.initializeConfigs(
            checkoutToken,
            phoneNumber,
            isDarkMode
        )
        val bazaarPayActivityArgs = BazaarPayActivityArgs(
            checkoutToken,
            phoneNumber,
            isDarkMode
        )
        Intent(context, BazaarPayActivity::class.java)
            .also {
                it.putExtra(BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
                activityResultLauncher.launch(it)
            }
    }

    fun onResultLauncher(
        result: ActivityResult,
        onSuccess: () -> Unit,
        onCancel: () -> Unit
    ) {
        when (result.resultCode) {
            Activity.RESULT_OK -> onSuccess()
            Activity.RESULT_CANCELED -> onCancel()
        }
    }
}

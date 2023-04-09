package ir.cafebazaar.bazaarpay

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs

/**
 * Handler object for launching Bazaar payment and parsing the result.
 *
 * Deprecated in favor of [StartBazaarPay].
 */
@Deprecated(
    message = "Use the StartBazaarPay class instead",
    level = DeprecationLevel.WARNING
)
object BazaarPayLauncher {

    /**
     * Launches BazaarPay flow.
     *
     * @param context the context in which payment happens.
     * @param checkoutToken the unique identifier that provides essential payment information.
     * @param isDarkMode enables *Dark-Mode* for the UI elements of the payment flow, which are in *Light-Mode* by default.
     * @param phoneNumber the default phone number to pre-fill the login screen's input field. It uses a `null` value by default, resulting in no pre-filled input.
     * @param activityResultLauncher an instance of the [ActivityResultLauncher] registered using the Activity Result API.
     */
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

    /**
     * Notifies BazaarPay about the incoming [ActivityResult] and parses the result.
     *
     * After parsing the result, either [onSuccess] or [onCancel] callbacks will be executed.
     *
     * @param result the incoming result from the [Activity].
     * @param onSuccess the callback when result shows a successful payment.
     * @param onCancel the callback when result shows payment canceled by the user.
     */
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

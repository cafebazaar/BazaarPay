package ir.cafebazaar.bazaarpay.directPay

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ir.cafebazaar.bazaarpay.BazaarPayActivity
import ir.cafebazaar.bazaarpay.ServiceLocator.initializeConfigsForDirectPayContract
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs

/**
 * An [ActivityResultContract] to start a payment.
 *
 * The input is a [DirectPayContractOptions] that configures the payment flow.
 *
 * Returns `true` if the contract was agreed by user.
 *
 * Example of usage:
 *
 * ```
 * val bazaarPayDirectPayContractLauncher = registerForActivityResult(startDirectPayFinalizeContract()) { isSuccessful ->
 *     if (isSuccessful) {
 *          // A successful contract.
 *     } else {
 *          // An unsuccessful contract (Or Canceled by the user).
 *     }
 * }
 *
 * val options = DirectPayContractOptions(contractToken = "CONTRACT_TOKEN")
 * bazaarPayDirectPayContractLauncher.launch(options)
 */
class StartDirectPayFinalizeContract : ActivityResultContract<DirectPayContractOptions, Boolean>() {

    override fun createIntent(context: Context, input: DirectPayContractOptions): Intent {
        initializeConfigsForDirectPayContract(
            phoneNumber = input.phoneNumber,
            contractToken = input.contractToken
        )
        val bazaarPayActivityArgs = BazaarPayActivityArgs.DirectPayContract(
            phoneNumber = input.phoneNumber,
            contractToken = input.contractToken
        )
        return Intent(context, BazaarPayActivity::class.java).apply {
            putExtra(BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}

package ir.cafebazaar.bazaarpay

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs
import ir.cafebazaar.bazaarpay.utils.getLanguage
import ir.cafebazaar.bazaarpay.utils.getLanguageNumber

class BazaarPayContract : ActivityResultContract<BazaarPayOptions, Boolean>() {

    override fun createIntent(context: Context, input: BazaarPayOptions): Intent {
        ServiceLocator.initializeConfigs(
            input.checkoutToken,
            input.phoneNumber,
            input.isInDarkMode,
            getLanguage(input.isEnglish),
            getLanguageNumber(input.isEnglish)
        )
        val bazaarPayActivityArgs = BazaarPayActivityArgs(
            input.checkoutToken,
            input.phoneNumber,
            input.isInDarkMode,
            getLanguage(input.isEnglish),
            getLanguageNumber(input.isEnglish)
        )
        return Intent(context, BazaarPayActivity::class.java).apply {
            putExtra(BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}

package ir.cafebazaar.bazaarpay

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.lang.Exception

class BazaarPayLauncher(
    private val context: Context,
    private val onSuccess: () -> Unit,
    private val onCancel: () -> Unit,
) {

    fun launchBazaarPay(
        checkoutToken: String,
        isDarkMode: Boolean = false,
        isEnglish: Boolean = false
    ) {
        ServiceLocator.initializeConfigs(
            checkoutToken,
            isDarkMode,
            getLanguage(isEnglish),
            getLanguageNumber(isEnglish)
        )
        Intent(context, BazaarPayActivity::class.java)
            .also {
                activityResultLauncher.launch(it)
            }
    }

    private val activityResultCaller: ActivityResultCaller
        get() {
            return if ((context is ActivityResultCaller).not()) {
                throw Exception(
                    "Context is not a type of ActivityResultCaller in" +
                            "BazaarPayLauncher class"
                )
            } else {
                context as ActivityResultCaller
            }
        }

    private var activityResultLauncher: ActivityResultLauncher<Intent> =
        activityResultCaller.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> onSuccess()
                RESULT_CANCELED -> onCancel()
            }
        }

    private fun getLanguage(isEnglish: Boolean): String {
        return if (isEnglish) {
            "en"
        } else {
            "fa"
        }
    }

    private fun getLanguageNumber(isEnglish: Boolean): Int {
        return if (isEnglish) {
            1
        } else {
            2
        }
    }
}
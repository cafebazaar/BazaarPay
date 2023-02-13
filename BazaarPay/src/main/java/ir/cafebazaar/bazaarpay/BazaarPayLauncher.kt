package ir.cafebazaar.bazaarpay

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ir.cafebazaar.bazaarpay.BazaarPayActivity.Companion.KEY_ERROR_MESSAGE
import ir.cafebazaar.bazaarpay.BazaarPayActivity.Companion.KEY_HTTP_ERROR_CODE
import ir.cafebazaar.bazaarpay.BazaarPayActivity.Companion.KEY_HTTP_ERROR_JSON
import ir.cafebazaar.bazaarpay.BazaarPayActivity.Companion.RESULT_ERROR
import ir.cafebazaar.bazaarpay.utils.getLanguage
import ir.cafebazaar.bazaarpay.utils.getLanguageNumber
import java.lang.Exception

class BazaarPayLauncher(
    private val context: Context,
    private val onSuccess: () -> Unit,
    private val onFailure: (String?, Int?, String?) -> Unit,
    private val onCancel: () -> Unit,
) {

    fun launchBazaarPay(
        checkoutToken: String,
        phoneNumber: String? = null,
        isDarkMode: Boolean = false,
        isEnglish: Boolean = false
    ) {
        ServiceLocator.initializeConfigs(
            checkoutToken,
            phoneNumber,
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
                RESULT_ERROR -> {
                    val message = result.data?.getStringExtra(KEY_ERROR_MESSAGE)
                    val errorCode = result.data?.getIntExtra(KEY_HTTP_ERROR_CODE, -1)
                    val errorJson = result.data?.getStringExtra(KEY_HTTP_ERROR_JSON)
                    onFailure(message, errorCode, errorJson)
                }
                RESULT_CANCELED -> onCancel()
            }
        }
}
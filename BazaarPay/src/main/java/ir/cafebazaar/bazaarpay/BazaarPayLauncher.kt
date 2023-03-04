package ir.cafebazaar.bazaarpay

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ir.cafebazaar.bazaarpay.BazaarPayActivity.Companion.BAZAARPAY_ACTIVITY_ARGS
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PurchaseStatus
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.utils.getLanguage
import ir.cafebazaar.bazaarpay.utils.getLanguageNumber
import java.lang.Exception

class BazaarPayLauncher(
    private val context: Context,
    private val onSuccess: () -> Unit,
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
        val bazaarPayActivityArgs = BazaarPayActivityArgs(
            checkoutToken,
            phoneNumber,
            isDarkMode,
            getLanguage(isEnglish),
            getLanguageNumber(isEnglish)
        )
        Intent(context, BazaarPayActivity::class.java)
            .also {
                it.putExtra(BAZAARPAY_ACTIVITY_ARGS, bazaarPayActivityArgs)
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
}

fun initSDKForAPICall(
    context: Context,
    checkoutToken: String
) {
    ServiceLocator.initializeConfigs(
        checkoutToken = checkoutToken,
        language = getLanguage(isEnglish = false),
        languageNumber = getLanguageNumber(isEnglish = false),
        isDark = false
    )
    ServiceLocator.initializeDependencies(context)
}

suspend fun commit(
    checkoutToken: String,
    context: Context,
    onSuccess: () -> Unit,
    onFailure: (ErrorModel) -> Unit
) {
    initSDKForAPICall(context, checkoutToken)
    val payRepository: PaymentRepository = ServiceLocator.get()
    payRepository.commit(checkoutToken).fold(
        ifSuccess = {
            onSuccess.invoke()
        },
        ifFailure = onFailure
    )
}

suspend fun trace(
    checkoutToken: String,
    context: Context,
    onSuccess: (PurchaseStatus) -> Unit,
    onFailure: (ErrorModel) -> Unit
) {
    initSDKForAPICall(context, checkoutToken)
    val payRepository: PaymentRepository = ServiceLocator.get()
    payRepository.trace(checkoutToken).fold(
        ifSuccess = {
            onSuccess.invoke(it)
        },
        ifFailure = onFailure
    )
}
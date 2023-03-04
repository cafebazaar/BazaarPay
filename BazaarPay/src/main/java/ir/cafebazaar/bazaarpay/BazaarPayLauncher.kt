package ir.cafebazaar.bazaarpay

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.utils.getLanguage
import ir.cafebazaar.bazaarpay.utils.getLanguageNumber

object BazaarPayLauncher {

    fun launchBazaarPay(
        context: Context,
        checkoutToken: String,
        phoneNumber: String? = null,
        isDarkMode: Boolean = false,
        isEnglish: Boolean = false,
        activityResultLauncher: ActivityResultLauncher<Intent>
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

    fun onResultLauncher(
        result: ActivityResult,
        onSuccess: () -> Unit,
        onCancel: () -> Unit
    ) {
        when (result.resultCode) {
            RESULT_OK -> onSuccess()
            RESULT_CANCELED -> onCancel()
        }
    }
}

suspend fun commit(
    checkoutToken: String,
    context: Context,
    onSuccess: () -> Unit,
    onFailure: (ErrorModel) -> Unit
) {
    ServiceLocator.initializeConfigs(
        checkoutToken = checkoutToken,
        language = getLanguage(isEnglish = false),
        languageNumber = getLanguageNumber(isEnglish = false),
        isDark = false
    )
    ServiceLocator.initializeDependencies(context)
    val payRepository: PaymentRepository = ServiceLocator.get()
    payRepository.commit(checkoutToken).fold(
        ifSuccess = {
            onSuccess.invoke()
        },
        ifFailure = onFailure
    )
}
package ir.cafebazaar.bazaarpay

import android.content.Context
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PurchaseStatus
import ir.cafebazaar.bazaarpay.extensions.fold

fun initSDKForAPICall(
    context: Context,
    checkoutToken: String
) {
    ServiceLocator.initializeConfigs(
        checkoutToken = checkoutToken,
        isDark = false
    )
    ServiceLocator.initializeDependencies(context)
}

/**
 * Commits the [checkoutToken] for a successful payment.
 *
 * @param checkoutToken the token to commit.
 * @param context the context in which commit happens.
 * @param onSuccess the callback when committed [checkoutToken] successfully.
 * @param onFailure the callback for an unsuccessful commit with [ErrorModel] to reason about the cause.
 */
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

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
 * It is better to call it from a WorkManager worker or a Service for safety reasons.
 *
 * Although sending tokens through the SDK is possible, we recommend this happens on the server side.
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

/**
 * Traces the current status of the payment using [checkoutToken].
 *
 * @param checkoutToken the token to trace payment status for.
 * @param context the context in which tracing happens.
 * @param onSuccess the callback when traced payment successfully offering its [PurchaseStatus].
 * @param onFailure the callback for an unsuccessful tracing with [ErrorModel] to reason about the cause.
 */
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

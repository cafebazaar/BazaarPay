package ir.cafebazaar.bazaarpay

import android.content.Context
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.pay.InitCheckoutResult
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
 * Commits the checkout process after a successful payment transaction.
 *
 * If the checkout is not committed within a certain period of time, it will be automatically refunded.
 *
 * The recommended way to call this API is server-side for security reasons.
 *
 * If called from a client-side, the developer should ensure that the commit process is finished
 * before the application is closed (e.g., by using a WorkManager worker or a Service).
 *
 * @param checkoutToken the token returned from the checkout API.
 * @param context the context in which commit happens.
 * @param onSuccess the callback when the transaction committed successfully.
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
 * Traces the current status of the checkout process.
 *
 * @param checkoutToken the token of checkout process.
 * @param context the context in which tracing happens.
 * @param onSuccess the callback when traced checkout process successfully offering its [PurchaseStatus].
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

/**
 * Init the [checkoutToken] in order to initiate a purchase flow.
 *
 * @param context the context in which tracing happens.
 * @param amount the amount of the purchase in Rials. Notice that this has some limitations which you should consider based on BazaarPay documentation.
 * @param destination the destination of the purchase which you should get it from the BazaarPay team.
 * @param serviceName the serviceName of the purchase which you should get it from the BazaarPay team.
 * @param onSuccess the callback when init checkout payment successfully offering its [InitCheckoutResult].
 * @param onFailure the callback for an unsuccessful initiating of checkout with [ErrorModel] to reason about the cause.
 */
suspend fun initCheckout(
    context: Context,
    amount: Long,
    destination: String,
    serviceName: String,
    onSuccess: (InitCheckoutResult) -> Unit,
    onFailure: (ErrorModel) -> Unit
) {
    initSDKForAPICall(
        context = context,
        checkoutToken = ""
    )
    val payRepository: PaymentRepository = ServiceLocator.get()
    payRepository.initCheckout(amount, destination, serviceName).fold(
        ifSuccess = {
            onSuccess.invoke(it)
        },
        ifFailure = onFailure
    )
}

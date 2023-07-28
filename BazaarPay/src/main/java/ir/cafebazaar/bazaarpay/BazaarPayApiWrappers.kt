package ir.cafebazaar.bazaarpay

import android.content.Context
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.models.pay.BalanceResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.InitCheckoutResult
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PurchaseStatus
import ir.cafebazaar.bazaarpay.extensions.fold

fun initSDKForAPICall(
    context: Context,
    checkoutToken: String,
    autoLoginPhoneNumber: String? = null,
    isAutoLoginEnable: Boolean = false,
) {
    ServiceLocator.initializeConfigsForNormal(
        checkoutToken = checkoutToken,
        isDark = false,
        autoLoginPhoneNumber = autoLoginPhoneNumber,
        isAutoLoginEnable = isAutoLoginEnable
    )
    ServiceLocator.initializeDependencies(context.applicationContext)
}

suspend fun commit(
    paymentURL: String,
    context: Context,
    onSuccess: () -> Unit,
    onFailure: (ErrorModel) -> Unit
) {
    val paymentURLParser = PaymentURLParser(paymentURL)
    val checkoutToken = paymentURLParser.getCheckoutToken() ?: paymentURL
    val autoLoginPhoneNumber = paymentURLParser.getAutoLoginPhoneNumber()
    val isAutoLoginEnable = paymentURLParser.isAutoLoginEnable()
    initSDKForAPICall(context, checkoutToken, autoLoginPhoneNumber, isAutoLoginEnable)
    val payRepository: PaymentRepository = ServiceLocator.get()
    payRepository.commit(checkoutToken).fold(
        ifSuccess = {
            onSuccess.invoke()
        },
        ifFailure = onFailure
    )
}

suspend fun trace(
    paymentURL: String,
    context: Context,
    onSuccess: (PurchaseStatus) -> Unit,
    onFailure: (ErrorModel) -> Unit
) {
    val paymentURLParser = PaymentURLParser(paymentURL)
    val checkoutToken = paymentURLParser.getCheckoutToken() ?: paymentURL
    val autoLoginPhoneNumber = paymentURLParser.getAutoLoginPhoneNumber()
    val isAutoLoginEnable = paymentURLParser.isAutoLoginEnable()
    initSDKForAPICall(context, checkoutToken, autoLoginPhoneNumber, isAutoLoginEnable)
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

/**
 * Get user balance
 *
 * @param context the context in which tracing happens.
 * @param onSuccess the callback when get balance is successfully fetched [BalanceResult].
 * @param onFailure the callback for an unsuccessful get balance with [ErrorModel] to reason about the cause.
 * @param onLoginNeeded the callback for a login issue, users should be logged in for fetching their balance.
 */
suspend fun getBazaarPayBalance(
    context: Context,
    onSuccess: (BalanceResult) -> Unit,
    onFailure: (ErrorModel) -> Unit,
    onLoginNeeded: () -> Unit = {},
) {
    initSDKForAPICall(
        context = context,
        checkoutToken = ""
    )
    val payRepository: PaymentRepository = ServiceLocator.get()
    val accountRepository: AccountRepository = ServiceLocator.get()

    if (accountRepository.needLogin()) {
        onLoginNeeded.invoke()
        return
    }
    payRepository.getBalance().fold(
        ifSuccess = onSuccess,
        ifFailure = onFailure
    )
}

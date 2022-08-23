package ir.cafebazaar.bazaarpay

import android.app.Activity
import android.content.Context
import ir.cafebazaar.bazaarpay.data.SharedDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountSharedDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountLocalDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRemoteDataSource
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.data.payment.AuthenticatorInterceptor
import ir.cafebazaar.bazaarpay.data.payment.PaymentRemoteDataSource
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.TokenInterceptor
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarRemoteDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarRepository
import ir.hamidbazargan.dynamicrestclient.base.Base
import ir.hamidbazargan.dynamicrestclient.client.Client
import ir.hamidbazargan.dynamicrestclient.getDefaultCache
import kotlinx.coroutines.Dispatchers
import okhttp3.Authenticator
import okhttp3.Interceptor

internal object ServiceLocator {

    val servicesMap = HashMap<String, Any>()

    fun isConfigInitiated(): Boolean {
        return (servicesMap[getKeyOfClass<String>(CHECKOUT_TOKEN)]) != null
    }

    fun initializeConfigs(
        checkoutToken: String,
        isDark: Boolean,
        language: String,
        languageNumber: Int
    ) {
        servicesMap[getKeyOfClass<String>(CHECKOUT_TOKEN)] = checkoutToken
        servicesMap[getKeyOfClass<Boolean>(IS_DARK)] = isDark
        servicesMap[getKeyOfClass<Int>(LANGUAGE)] = languageNumber
        servicesMap[getKeyOfClass<String>(LANGUAGE)] = language
    }

    fun initializeDependencies(
        activity: Activity
    ) {
        servicesMap[getKeyOfClass<Context>()] = activity
        initGlobalDispatchers()
        initAccountClient()
        initAccountSharedDataSource()
        initAccountLocalDataSource()
        initAccountRemoteDataSource()
        initAccountRepository()
        initAuthenticator()
        initTokenInterceptor()
        initPaymentClient()
        initPaymentRemoteDataSource()
        initPaymentRepository()
        initBazaarClient()
        initBazaarRemoteDataSource()
        initBazaarRepository()
    }

    fun clear() {
        servicesMap.clear()
    }

    private fun initGlobalDispatchers() {
        servicesMap[getKeyOfClass<GlobalDispatchers>()] = GlobalDispatchers(
            Dispatchers.Main,
            Dispatchers.IO,
            Dispatchers.Default
        )
    }

    private fun initAccountClient() {
        servicesMap[getKeyOfClass<Base>(ACCOUNT)] = Client
            .builder()
            .withDebugMode(true)
            .withCache(get<Context>().getDefaultCache())
            .build()
            .buildBase(
                DEFAULT_BASE_URL,
                getUserAgentHeader()
            )
    }

    private fun getUserAgentHeader(): HashMap<String, Any?> {
        return hashMapOf(USER_AGENT_HEADER_TITLE to getUserAgentHeaderValue())
    }

    private fun initAccountSharedDataSource() {
        servicesMap[getKeyOfClass<SharedDataSource>(ACCOUNT)] =
            AccountSharedDataSource()
    }

    private fun initAccountLocalDataSource() {
        servicesMap[getKeyOfClass<AccountLocalDataSource>()] = AccountLocalDataSource()
    }

    private fun initAccountRemoteDataSource() {
        servicesMap[getKeyOfClass<AccountRemoteDataSource>()] = AccountRemoteDataSource()
    }

    private fun initAccountRepository() {
        servicesMap[getKeyOfClass<AccountRepository>()] = AccountRepository()
    }

    private fun initAuthenticator() {
        servicesMap[getKeyOfClass<Authenticator>(AUTHENTICATOR)] = AuthenticatorInterceptor()
    }

    private fun initTokenInterceptor() {
        servicesMap[getKeyOfClass<Interceptor>(TOKEN)] = TokenInterceptor()
    }

    private fun initPaymentClient() {
        servicesMap[getKeyOfClass<Base>(PAYMENT)] = Client
            .builder()
            .withDebugMode(true)
            .withCache(get<Context>().getDefaultCache())
            .withAuthenticator(get(AUTHENTICATOR))
            .withInterceptor(get(TOKEN))
            .build()
            .buildBase(
                PAYMENT_BASE_URL,
                getUserAgentHeader()
            )
    }

    private fun initPaymentRemoteDataSource() {
        servicesMap[getKeyOfClass<PaymentRemoteDataSource>()] = PaymentRemoteDataSource()
    }

    private fun initPaymentRepository() {
        servicesMap[getKeyOfClass<PaymentRepository>()] = PaymentRepository()
    }

    private fun initBazaarClient() {
        servicesMap[getKeyOfClass<Base>(BAZAAR)] = Client
            .builder()
            .withDebugMode(true)
            .withCache(get<Context>().getDefaultCache())
            .withAuthenticator(get(AUTHENTICATOR))
            .withInterceptor(get(TOKEN))
            .build()
            .buildBase(
                DEFAULT_BASE_URL,
                getUserAgentHeader()
            )
    }

    private fun initBazaarRemoteDataSource() {
        servicesMap[getKeyOfClass<BazaarRemoteDataSource>()] = BazaarRemoteDataSource()
    }

    private fun initBazaarRepository() {
        servicesMap[getKeyOfClass<BazaarRepository>()] = BazaarRepository()
    }

    inline fun <reified T> get(named: String = ""): T {
        return servicesMap[getKeyOfClass<T>() + named] as T
    }

    inline fun <reified T> getOrNull(named: String = ""): T? {
        return servicesMap[getKeyOfClass<T>() + named] as? T
    }

    inline fun <reified T> getKeyOfClass(named: String = ""): String {
        return T::class.java.name + named
    }

    private fun getUserAgentHeaderValue(): String {
        return "BazaarPayAndroidSDK/" +
                "${BuildConfig.VERSION} " +
                "${get<Context>().packageName}/" +
                getAppVersionName()
    }

    private fun getAppVersionName(): String {
        return get<Context>().packageManager
            .getPackageInfo(get<Context>().packageName, 0).versionName
    }

    private const val DEFAULT_BASE_URL: String = "https://api.cafebazaar.ir/"
    private const val PAYMENT_BASE_URL: String = "https://pardakht.cafebazaar.ir/"
    private const val USER_AGENT_HEADER_TITLE: String = "UserAgent"
    internal const val CHECKOUT_TOKEN: String = "checkout_token"
    internal const val IS_DARK: String = "is_dark"
    internal const val LANGUAGE: String = "language"
    internal const val ACCOUNT: String = "account"
    internal const val PAYMENT: String = "payment"
    internal const val BAZAAR: String = "bazaar"
    private const val AUTHENTICATOR: String = "authenticator"
    private const val TOKEN: String = "token"
}
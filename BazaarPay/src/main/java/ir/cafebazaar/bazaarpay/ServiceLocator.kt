package ir.cafebazaar.bazaarpay

import android.content.Context
import com.google.gson.GsonBuilder
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.data.SharedDataSource
import ir.cafebazaar.bazaarpay.data.analytics.AnalyticsRemoteDataSource
import ir.cafebazaar.bazaarpay.data.analytics.AnalyticsRepository
import ir.cafebazaar.bazaarpay.data.analytics.api.AnalyticsService
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountLocalDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRemoteDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountService
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountSharedDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.UserInfoService
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarPaymentRemoteDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarPaymentRepository
import ir.cafebazaar.bazaarpay.data.bazaar.payment.api.BazaarPaymentService
import ir.cafebazaar.bazaarpay.data.device.DeviceInterceptor
import ir.cafebazaar.bazaarpay.data.device.DeviceLocalDataSource
import ir.cafebazaar.bazaarpay.data.device.DeviceRepository
import ir.cafebazaar.bazaarpay.data.device.DeviceSharedDataSource
import ir.cafebazaar.bazaarpay.data.directPay.DirectPayRemoteDataSource
import ir.cafebazaar.bazaarpay.data.directPay.api.DirectPayService
import ir.cafebazaar.bazaarpay.data.payment.AuthenticatorInterceptor
import ir.cafebazaar.bazaarpay.data.payment.PaymentRemoteDataSource
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.TokenInterceptor
import ir.cafebazaar.bazaarpay.data.payment.UpdateRefreshTokenHelper
import ir.cafebazaar.bazaarpay.data.payment.api.PaymentService
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.network.interceptor.HeaderInterceptor
import kotlinx.coroutines.Dispatchers
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object ServiceLocator {

    val servicesMap = HashMap<String, Any?>()

    fun initializeConfigsForNormal(
        checkoutToken: String,
        phoneNumber: String? = null,
        autoLoginPhoneNumber: String? = null,
        isAutoLoginEnable: Boolean = false,
        autoLoginAuthToken: String? = null,
        isAccessibilityEnable: Boolean = false,
    ) {
        servicesMap[getKeyOfClass<String>(CHECKOUT_TOKEN)] = checkoutToken
        servicesMap[getKeyOfClass<String?>(PHONE_NUMBER)] = phoneNumber
        servicesMap[getKeyOfClass<String>(AUTO_LOGIN_PHONE_NUMBER)] = autoLoginPhoneNumber
        servicesMap[getKeyOfClass<Boolean>(IS_AUTO_LOGIN_ENABLE)] = isAutoLoginEnable
        servicesMap[getKeyOfClass<Boolean>(IS_ACCESSIBILITY_ENABLE)] = isAccessibilityEnable
        Analytics.setCheckOutToken(checkoutToken)
        Analytics.setAutoLoginState(isAutoLoginEnable || autoLoginAuthToken.isNullOrEmpty().not())
        initializeShareConfigs(autoLoginAuthToken)
    }

    fun initializeConfigsForDirectPayContract(
        contractToken: String,
        phoneNumber: String? = null,
        message: String? = null,
        authToken: String? = null,
    ) {
        servicesMap[getKeyOfClass<String?>(DIRECT_PAY_CONTRACT_TOKEN)] = contractToken
        servicesMap[getKeyOfClass<String?>(PHONE_NUMBER)] = phoneNumber
        servicesMap[getKeyOfClass<String?>(DIRECT_PAY_MERCHANT_MESSAGE)] = message
        initializeShareConfigs(authToken)
    }

    fun initializeConfigsForLogin(phoneNumber: String? = null) {
        servicesMap[getKeyOfClass<String?>(PHONE_NUMBER)] = phoneNumber
        initializeShareConfigs()
    }

    fun initializeShareConfigs(authToken: String? = null) {
        servicesMap[getKeyOfClass<Int>(LANGUAGE)] = FA_LANGUAGE
        servicesMap[getKeyOfClass<String>(LANGUAGE)] = "fa"
        servicesMap[getKeyOfClass<Boolean>(IS_DARK)] = null
        authToken?.let { token ->
            servicesMap[getKeyOfClass<String>(AUTO_LOGIN_TOKEN)] = token
        }
    }

    fun initializeDependencies(
        context: Context
    ) {
        servicesMap[getKeyOfClass<Context>()] = context

        // Device
        initDeviceSharedDataSource()
        initDeviceLocalDataSource()
        initDeviceRepository()

        initDeviceInterceptor()
        initGlobalDispatchers()
        initHttpLoggingInterceptor()

        // Account
        initAccountService()
        initAccountSharedDataSource()
        initAccountLocalDataSource()
        initAccountRemoteDataSource()
        initAccountRepository()

        // Auth
        initUpdateRefreshTokenHelper()
        initAuthenticator()
        initTokenInterceptor()

        // Payment
        initRetrofitServices()
        initPaymentRemoteDataSource()
        initPaymentRepository()

        //directPay
        initDirectPayDataSource()

        // Bazaar
        initBazaarRemoteDataSource()
        initBazaarRepository()

        //analytics
        initAnalyticsRemoteDataSource()
        initAnalyticsRepository()
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

    private fun initAnalyticsRemoteDataSource() {
        servicesMap[getKeyOfClass<AnalyticsRemoteDataSource>()] = AnalyticsRemoteDataSource()
    }

    private fun initAnalyticsRepository() {
        servicesMap[getKeyOfClass<AnalyticsRepository>()] = AnalyticsRepository()
    }

    private fun initAccountRepository() {
        servicesMap[getKeyOfClass<AccountRepository>()] = AccountRepository()
    }

    private fun initDeviceLocalDataSource() {
        servicesMap[getKeyOfClass<DeviceLocalDataSource>()] = DeviceLocalDataSource()
    }

    private fun initDeviceRepository() {
        servicesMap[getKeyOfClass<DeviceRepository>()] = DeviceRepository()
    }

    private fun initUpdateRefreshTokenHelper() {
        servicesMap[getKeyOfClass<UpdateRefreshTokenHelper>()] = UpdateRefreshTokenHelper()
    }

    private fun initAuthenticator() {
        servicesMap[getKeyOfClass<Authenticator>(AUTHENTICATOR)] = AuthenticatorInterceptor()
    }

    private fun initTokenInterceptor() {
        servicesMap[getKeyOfClass<Interceptor>(TOKEN)] = TokenInterceptor()
    }

    private fun initDeviceInterceptor() {
        servicesMap[getKeyOfClass<DeviceInterceptor>()] = DeviceInterceptor()
    }

    private fun initHttpLoggingInterceptor() {
        servicesMap[getKeyOfClass<HttpLoggingInterceptor>()] = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun initPaymentRemoteDataSource() {
        servicesMap[getKeyOfClass<PaymentRemoteDataSource>()] = PaymentRemoteDataSource()
    }

    private fun initDirectPayDataSource() {
        servicesMap[getKeyOfClass<DirectPayRemoteDataSource>()] = DirectPayRemoteDataSource()
    }

    private fun initPaymentRepository() {
        servicesMap[getKeyOfClass<PaymentRepository>()] = PaymentRepository()
    }

    private fun initBazaarRemoteDataSource() {
        servicesMap[getKeyOfClass<BazaarPaymentRemoteDataSource>()] = BazaarPaymentRemoteDataSource()
    }

    private fun initBazaarRepository() {
        servicesMap[getKeyOfClass<BazaarPaymentRepository>()] = BazaarPaymentRepository()
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

    private const val REQUEST_TIME_OUT: Long = 60
    private fun provideOkHttpClient(
        interceptors: List<Interceptor> = emptyList(),
        authenticator: Authenticator? = null
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()

        authenticator?.let {
            builder.authenticator(it)
        }
        builder
            .addInterceptor(HeaderInterceptor)
            .addInterceptor(get<DeviceInterceptor>())

        interceptors.forEach {
            builder.addInterceptor(it)
        }

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = get<HttpLoggingInterceptor?>()
            if (loggingInterceptor != null) {
                builder.addInterceptor(loggingInterceptor)
            }
        }

        return builder
            .connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    private fun provideRetrofit(
        okHttp: OkHttpClient,
        baseUrl: String,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().create())
            )
            .client(okHttp)
            .build()
    }

    private fun initAccountService() {
        val accountHttpClient = provideOkHttpClient()
        val accountRetrofit = provideRetrofit(
            okHttp = accountHttpClient,
            baseUrl = BASE_URL
        )
        servicesMap[getKeyOfClass<AccountService>()] =
            accountRetrofit.create(AccountService::class.java)
    }

    private fun initRetrofitServices() {
        val okHttpClient = provideOkHttpClient(
            interceptors = listOf(get(TOKEN)),
            authenticator = get<Authenticator?>(AUTHENTICATOR).takeIf {
                isUserLogOutAndAutoLoginEnable().not()
            }
        )
        val retrofit = provideRetrofit(
            okHttp = okHttpClient,
            baseUrl = BASE_URL
        )
        servicesMap[getKeyOfClass<PaymentService>()] =
            retrofit.create(PaymentService::class.java)

        servicesMap[getKeyOfClass<DirectPayService>()] =
            retrofit.create(DirectPayService::class.java)

        servicesMap[getKeyOfClass<BazaarPaymentService>()] =
            retrofit.create(BazaarPaymentService::class.java)

        servicesMap[getKeyOfClass<AnalyticsService>()] =
            retrofit.create(AnalyticsService::class.java)

        servicesMap[getKeyOfClass<UserInfoService>()] =
            retrofit.create(UserInfoService::class.java)
    }

    private fun initDeviceSharedDataSource() {
        servicesMap[getKeyOfClass<SharedDataSource>(DEVICE)] =
            DeviceSharedDataSource()
    }

    private fun isUserLogOutAndAutoLoginEnable(): Boolean {
        val accountLocalDataSource: AccountLocalDataSource = get()
        val isOldAutoLoginEnable = getOrNull<Boolean>(IS_AUTO_LOGIN_ENABLE) ?: false
        val isNewAutoLoginEnable = getOrNull<String>(AUTO_LOGIN_TOKEN).isNullOrEmpty().not()
        val isAutoLoginEnable = isOldAutoLoginEnable || isNewAutoLoginEnable

        fun isLoggedIn(): Boolean {
            return accountLocalDataSource.accessToken.isNotEmpty()
        }
        return isAutoLoginEnable && isLoggedIn().not()
    }

    internal fun isAccessibilityEnable(): Boolean {
        return getOrNull<Boolean>(IS_ACCESSIBILITY_ENABLE) ?: false
    }

    private const val BASE_URL: String = "https://staging.api.bazaar-pay.ir/"

    internal const val CHECKOUT_TOKEN: String = "checkout_token"
    internal const val PHONE_NUMBER: String = "phone_number"
    internal const val DIRECT_PAY_MERCHANT_MESSAGE: String = "direct_pay_merchant_message"
    internal const val DIRECT_PAY_CONTRACT_TOKEN: String = "direct-debit-contract-token"
    internal const val IS_DARK: String = "is_dark"
    internal const val AUTO_LOGIN_TOKEN: String = "auto_login_token"
    internal const val LANGUAGE: String = "language"
    internal const val AUTO_LOGIN_PHONE_NUMBER: String = "autoLoginPhoneNumber"
    internal const val IS_AUTO_LOGIN_ENABLE: String = "isAutoLoginEnable"
    private const val IS_ACCESSIBILITY_ENABLE: String = "isAccessibilityEnable"
    internal const val ACCOUNT: String = "account"
    internal const val DEVICE: String = "device"
    private const val AUTHENTICATOR: String = "authenticator"
    private const val TOKEN: String = "token"
    internal const val FA_LANGUAGE = 2
}

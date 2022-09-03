package ir.cafebazaar.bazaarpay

import android.content.Context
import com.google.gson.GsonBuilder
import ir.cafebazaar.bazaarpay.data.SharedDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountSharedDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountLocalDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRemoteDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountService
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.data.payment.AuthenticatorInterceptor
import ir.cafebazaar.bazaarpay.data.payment.PaymentRemoteDataSource
import ir.cafebazaar.bazaarpay.data.payment.PaymentRepository
import ir.cafebazaar.bazaarpay.data.payment.TokenInterceptor
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarRemoteDataSource
import ir.cafebazaar.bazaarpay.data.bazaar.payment.BazaarRepository
import ir.cafebazaar.bazaarpay.data.bazaar.payment.api.BazaarPaymentService
import ir.cafebazaar.bazaarpay.data.payment.api.PaymentService
import ir.cafebazaar.bazaarpay.network.gsonConverterFactory
import ir.cafebazaar.bazaarpay.network.interceptor.AgentInterceptor
import kotlinx.coroutines.Dispatchers
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
        context: Context
    ) {
        servicesMap[getKeyOfClass<Context>()] = context
        initGlobalDispatchers()
        initJsonConvertorFactory()

        // Account
        initAccountService()
        initAccountSharedDataSource()
        initAccountLocalDataSource()
        initAccountRemoteDataSource()
        initAccountRepository()

        // Auth
        initAuthenticator()
        initTokenInterceptor()

        // Payment
        initPaymentService()
        initPaymentRemoteDataSource()
        initPaymentRepository()

        // Bazaar
        initBazaarService()
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

    private fun initJsonConvertorFactory() {
        servicesMap[getKeyOfClass<Converter.Factory>()] = gsonConverterFactory()
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

    private fun initPaymentRemoteDataSource() {
        servicesMap[getKeyOfClass<PaymentRemoteDataSource>()] = PaymentRemoteDataSource()
    }

    private fun initPaymentRepository() {
        servicesMap[getKeyOfClass<PaymentRepository>()] = PaymentRepository()
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

    private const val REQUEST_TIME_OUT: Long = 60
    private fun provideOkHttpClient(
        interceptors: List<Interceptor> = emptyList(),
        authenticator: Authenticator?= null
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
//        if (BuildConfig.DEBUG) {
//
//            val loggingInterceptor = get<HttpLoggingInterceptor?>()
//            if (loggingInterceptor != null) {
//                builder.addInterceptor(loggingInterceptor)
//            }
//        }
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        authenticator?.let {
            builder.authenticator(it)
        }
        builder.addInterceptor(AgentInterceptor)

        interceptors.forEach {
            builder.addInterceptor(it)
        }
        builder.addInterceptor(loggingInterceptor)

        return builder
            .connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    private fun provideRetrofit(
        okHttp: OkHttpClient,
        baseUrl: String = DEFAULT_BASE_URL,
        needUnWrapper: Boolean = false
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                if (needUnWrapper) {
                    get<Converter.Factory>()
                } else {
                    GsonConverterFactory.create(GsonBuilder().create())
                }
            )
            .client(okHttp)
            .build()
    }

    private fun initAccountService() {
        val accountHttpClient = provideOkHttpClient()
        val accountRetrofit = provideRetrofit(
            okHttp = accountHttpClient,
            needUnWrapper = false
        )
        servicesMap[getKeyOfClass<AccountService>()] =
            accountRetrofit.create(AccountService::class.java)
    }

    private fun initPaymentService() {
        val paymentHttpClient = provideOkHttpClient(
            interceptors = listOf(get(TOKEN)),
            authenticator = get(AUTHENTICATOR)
        )
        val paymentRetrofit = provideRetrofit(
            okHttp = paymentHttpClient,
            baseUrl = PAYMENT_BASE_URL
        )
        servicesMap[getKeyOfClass<PaymentService>()] =
            paymentRetrofit.create(PaymentService::class.java)
    }

    private fun initBazaarService() {
        val bazaarHttpClient = provideOkHttpClient(
            interceptors = listOf(get(TOKEN)),
            authenticator = get(AUTHENTICATOR)
        )
        val bazaarRetrofit = provideRetrofit(
            okHttp = bazaarHttpClient,
            baseUrl = DEFAULT_BASE_URL,
            needUnWrapper = false
        )
        servicesMap[getKeyOfClass<BazaarPaymentService>()] =
            bazaarRetrofit.create(BazaarPaymentService::class.java)
    }

    private const val DEFAULT_BASE_URL: String = "https://api.cafebazaar.ir/rest-v1/process/"
    private const val PAYMENT_BASE_URL: String = "https://pardakht.cafebazaar.ir/pardakht/badje/v1/"

    internal const val CHECKOUT_TOKEN: String = "checkout_token"
    internal const val IS_DARK: String = "is_dark"
    internal const val LANGUAGE: String = "language"
    internal const val ACCOUNT: String = "account"
    private const val AUTHENTICATOR: String = "authenticator"
    private const val TOKEN: String = "token"
}
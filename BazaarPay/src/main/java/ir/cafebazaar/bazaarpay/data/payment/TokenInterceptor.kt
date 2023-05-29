package ir.cafebazaar.bazaarpay.data.payment

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class TokenInterceptor : Interceptor {

    private val accountRepository: AccountRepository = ServiceLocator.get()

    private val isAutoLoginEnable by lazy {
        ServiceLocator.getOrNull<Boolean>(ServiceLocator.IS_AUTO_LOGIN_ENABLE) ?: false
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = accountRepository.getAccessToken()
        val isLoggedIn = accountRepository.isLoggedIn()

        if (alreadyHasAuthorizationHeader(originalRequest) || !isLoggedIn) {
            return chain.proceed(originalRequest)
        }

        val requestBuilder = originalRequest.newBuilder().apply {
            if (isAutoLoginEnable.not()) {
                header(AUTH_TOKEN_KEY, "Bearer $accessToken")
            }
        }.method(originalRequest.method, originalRequest.body)

        return chain.proceed(requestBuilder.build())
    }

    private fun alreadyHasAuthorizationHeader(request: Request): Boolean {
        return !request.header(AUTH_TOKEN_KEY).isNullOrEmpty()
    }

    companion object {

        const val AUTH_TOKEN_KEY = "Authorization"
    }
}
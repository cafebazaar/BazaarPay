package ir.cafebazaar.bazaarpay.data.payment

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.extensions.getFailureOrNull
import ir.cafebazaar.bazaarpay.extensions.getOrNull
import ir.cafebazaar.bazaarpay.extensions.isSuccessFull
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.payment.TokenInterceptor.Companion.AUTH_TOKEN_KEY
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

internal class AuthenticatorInterceptor: Authenticator {

    private val accountRepository: AccountRepository = ServiceLocator.get()
    private val updateRefreshTokenHelper: UpdateRefreshTokenHelper = ServiceLocator.get()

    override fun authenticate(route: Route?, response: Response): Request? {

        if (accountRepository.isLoggedIn()) {
            synchronized(LOCK) {
                return if (updateRefreshTokenHelper.needToUpdateRefreshToken) {
                    getRequestWithSendingRefreshToken(response)
                } else {
                    response.request.newBuilder().addAuthToken(
                        accountRepository.getAccessToken()
                    ).build()
                }
            }
        }

        return null
    }

    private fun getRequestWithSendingRefreshToken(response: Response): Request? {
        val refreshTokenEither = accountRepository.refreshAccessToken()
        val isRefreshTokenEmpty = refreshTokenEither.getOrNull().isNullOrEmpty()
        val isAuthenticationError =
            refreshTokenEither.getFailureOrNull() == ErrorModel.AuthenticationError
        return if (refreshTokenEither.isSuccessFull() && !isRefreshTokenEmpty) {
            updateRefreshTokenHelper.onRefreshTokenUpdated()
            val newAccessToken = refreshTokenEither.getOrNull()
            response.request.newBuilder().addAuthToken(newAccessToken).build()
        } else if (isAuthenticationError) {
            runBlocking { accountRepository.logout() }
            null
        } else {
            null
        }
    }

    private fun Request.Builder.addAuthToken(authToken: String?): Request.Builder {
        return header(AUTH_TOKEN_KEY, "Bearer $authToken")
    }

    companion object {
        private val LOCK = Any()
    }
}
package ir.cafebazaar.bazaarpay.data.bazaar.account

import android.content.Intent
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.ServiceLocator.AUTO_LOGIN_PHONE_NUMBER
import ir.cafebazaar.bazaarpay.ServiceLocator.IS_AUTO_LOGIN_ENABLE
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.WaitingTime
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext

internal class AccountRepository {

    private val globalDispatchers: GlobalDispatchers by lazy {
        ServiceLocator.get()
    }

    private val _onSmsPermissionSharedFlow: MutableSharedFlow<Intent> = MutableSharedFlow()
    val onSmsPermissionSharedFlow: SharedFlow<Intent> = _onSmsPermissionSharedFlow
    private val accountLocalDataSource: AccountLocalDataSource = ServiceLocator.get()
    private val accountRemoteDataSource: AccountRemoteDataSource = ServiceLocator.get()

    fun isLoggedIn(): Boolean {
        return accountLocalDataSource.accessToken.isNotEmpty()
    }

    fun needLogin(): Boolean {
        return if (isLoggedIn()) {
            false
        } else {
            ServiceLocator.getOrNull<Boolean>(IS_AUTO_LOGIN_ENABLE) ?: true
        }
    }

    suspend fun getAutoFillPhones(): List<String> {
        return withContext(globalDispatchers.iO) {
            return@withContext accountLocalDataSource.getAutoFillPhones()
        }
    }

    fun savePhone(phone: String) {
        accountLocalDataSource.putAutoFillPhones(phone)
    }

    fun getPhone(): String {
        val autoLoginPhoneNumber = ServiceLocator.get<String?>(AUTO_LOGIN_PHONE_NUMBER)
        return autoLoginPhoneNumber ?: accountLocalDataSource.loginPhone
    }

    suspend fun getOtpToken(phoneNumber: String):
            Either<WaitingTimeWithEnableCall> {
        return accountRemoteDataSource.getOtpToken(phoneNumber)
    }

    suspend fun getOtpTokenByCall(phoneNumber: String): Either<WaitingTime> {
        return accountRemoteDataSource.getOtpTokenByCall(phoneNumber)
    }

    suspend fun verifyOtpToken(phoneNumber: String, code: String): Either<LoginResponse> {
        return accountRemoteDataSource.verifyOtpToken(phoneNumber, code)
    }

    suspend fun setSmsPermissionObservable(intent: Intent) {
        _onSmsPermissionSharedFlow.emit(intent)
    }

    fun saveRefreshToken(refreshToken: String) {
        accountLocalDataSource.refreshToken = refreshToken
    }

    fun saveAccessToken(accessToken: String) {
        accountLocalDataSource.accessToken = accessToken
    }

    fun getRefreshToken(): String {
        return accountLocalDataSource.refreshToken
    }

    fun getAccessToken(): String {
        return accountLocalDataSource.accessToken
    }

    fun refreshAccessToken(): Either<String> {
        accountRemoteDataSource.getAccessToken(accountLocalDataSource.refreshToken)
            .fold(
                { token ->
                    if (token.isEmpty().not()) {
                        saveAccessToken(token)
                        accountLocalDataSource.accessTokenTimestamp = System.currentTimeMillis()
                    }
                    return Either.Success(token)
                },
                { error ->
                    return Either.Failure(error)
                }
            )
    }

    fun logout() {
        accountLocalDataSource.removeAccessToken()
        accountLocalDataSource.removeRefreshToken()
    }

    fun needToUpdateRefreshToken(): Boolean {
        return System.currentTimeMillis() - accountLocalDataSource.accessTokenTimestamp > EXPIRE_TIME
    }

    private companion object {

        const val EXPIRE_TIME = 50.000
    }
}
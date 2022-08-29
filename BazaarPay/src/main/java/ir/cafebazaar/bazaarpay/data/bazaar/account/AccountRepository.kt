package ir.cafebazaar.bazaarpay.data.bazaar.account

import android.content.Intent
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.WaitingTime
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

internal class AccountRepository {

    private val onSmsPermissionSharedFlow: MutableSharedFlow<Intent> = MutableSharedFlow()
    private val accountLocalDataSource: AccountLocalDataSource = ServiceLocator.get()
    private val accountRemoteDataSource: AccountRemoteDataSource = ServiceLocator.get()

    fun isLoggedIn(): Boolean {
        return accountLocalDataSource.accessToken.isNotEmpty()
    }

    fun getAutoFillPhones(): List<String> {
        return accountLocalDataSource.getAutoFillPhones()
    }

    fun savePhone(phone: String) {
        return accountLocalDataSource.putAutoFillPhones(phone)
    }

    fun getOtpToken(phoneNumber: String):
            Either<WaitingTimeWithEnableCall> {
        return accountRemoteDataSource.getOtpToken(phoneNumber)
    }

    fun getOtpTokenByCall(phoneNumber: String): Either<WaitingTime> {
        return accountRemoteDataSource.getOtpTokenByCall(phoneNumber)
    }

    fun verifyOtpToken(phoneNumber: String, code: String): Either<LoginResponse> {
        return accountRemoteDataSource.verifyOtpToken(phoneNumber, code)
    }

    fun getSmsPermissionObservable(): SharedFlow<Intent> {
        return onSmsPermissionSharedFlow
    }

    suspend fun setSmsPermissionObservable(intent: Intent) {
        onSmsPermissionSharedFlow.emit(intent)
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
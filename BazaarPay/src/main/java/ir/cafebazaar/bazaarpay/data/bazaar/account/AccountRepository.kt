package ir.cafebazaar.bazaarpay.data.bazaar.account

import android.content.Intent
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.ServiceLocator.AUTO_LOGIN_PHONE_NUMBER
import ir.cafebazaar.bazaarpay.ServiceLocator.IS_AUTO_LOGIN_ENABLE
import ir.cafebazaar.bazaarpay.ServiceLocator.getKeyOfClass
import ir.cafebazaar.bazaarpay.ServiceLocator.servicesMap
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.WaitingTime
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.extensions.getOrNull
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import ir.cafebazaar.bazaarpay.utils.doOnSuccess
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
        return when {
            isLoggedIn() -> false
            isNewAutoLoginEnable() -> false
            isOldAutoLoginEnable() -> false
            else -> true
        }
    }

    fun isOldAutoLoginEnable(): Boolean {
        return ServiceLocator.getOrNull<Boolean>(IS_AUTO_LOGIN_ENABLE) ?: false
    }

    fun isNewAutoLoginEnable(): Boolean {
        return ServiceLocator.getOrNull<String>(ServiceLocator.AUTO_LOGIN_TOKEN)
            .isNullOrEmpty()
            .not()
    }

    fun isAutoLoginEnable(): Boolean {
        return isOldAutoLoginEnable() || isNewAutoLoginEnable()
    }

    suspend fun getAutoFillPhones(): List<String> {
        return withContext(globalDispatchers.iO) {
            return@withContext accountLocalDataSource.getAutoFillPhones()
        }
    }

    fun savePhone(phone: String) {
        accountLocalDataSource.putAutoFillPhones(phone)
    }

    suspend fun getPhone(): String {
        return accountLocalDataSource.loginPhone.ifEmpty {
            if (isNewAutoLoginEnable()) {
                accountRemoteDataSource.getAutoLoginUserInfo().getOrNull()?.phoneNumber.orEmpty()
            } else {
                ServiceLocator.get<String?>(AUTO_LOGIN_PHONE_NUMBER).orEmpty()
            }
        }
    }

    suspend fun getOtpToken(phoneNumber: String):
            Either<WaitingTimeWithEnableCall> {
        return accountRemoteDataSource.getOtpToken(phoneNumber)
    }

    suspend fun getOtpTokenByCall(phoneNumber: String): Either<WaitingTime> {
        return accountRemoteDataSource.getOtpTokenByCall(phoneNumber)
    }

    suspend fun verifyOtpToken(phoneNumber: String, code: String): Either<LoginResponse> {
        return accountRemoteDataSource.verifyOtpToken(phoneNumber, code).doOnSuccess { response ->
            saveRefreshToken(response.refreshToken)
            saveAccessToken(response.accessToken)
            savePhone(phoneNumber)
            getUserInfoIfNeeded()
        }
    }

    suspend fun getUserInfoIfNeeded() {
        if (accountLocalDataSource.accountId.isEmpty() && isAutoLoginEnable().not()) {
            accountRemoteDataSource.getUserAccountId().doOnSuccess {
                accountLocalDataSource.accountId = it
            }
        }
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

    fun getAccountId(): String {
        return accountLocalDataSource.accountId
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
        accountLocalDataSource.removePhoneNumber()
        accountLocalDataSource.removeAccountId()
        servicesMap[getKeyOfClass<String>(AUTO_LOGIN_PHONE_NUMBER)] = null
        servicesMap[getKeyOfClass<Boolean>(IS_AUTO_LOGIN_ENABLE)] = false
    }

    fun needToUpdateRefreshToken(): Boolean {
        return System.currentTimeMillis() - accountLocalDataSource.accessTokenTimestamp > EXPIRE_TIME
    }

    private companion object {

        const val EXPIRE_TIME = 50.000
    }
}
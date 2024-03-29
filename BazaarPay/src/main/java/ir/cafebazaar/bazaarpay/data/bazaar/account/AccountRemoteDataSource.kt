package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.request.GetOtpTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.WaitingTime
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.request.GetOtpTokenByCallSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.request.GetAccessTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo.UserInfo
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.userinfo.toAutoLoginUserInfo
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.request.VerifyOtpTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

internal class AccountRemoteDataSource {

    private val checkoutToken: String? by lazy { ServiceLocator.getOrNull(ServiceLocator.CHECKOUT_TOKEN) }
    private val accountService: AccountService by lazy { ServiceLocator.get() }
    private val userInfoService: UserInfoService? by lazy { ServiceLocator.getOrNull() }
    private val globalDispatchers: GlobalDispatchers by lazy { ServiceLocator.get() }

    suspend fun getOtpToken(phoneNumber: String): Either<WaitingTimeWithEnableCall> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                accountService.getOtpToken(
                    GetOtpTokenSingleRequest(phoneNumber)
                ).toWaitingTimeWithEnableCall()
            }
        }
    }

    suspend fun getOtpTokenByCall(phoneNumber: String): Either<WaitingTime> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                accountService.getOtpTokenByCall(
                    GetOtpTokenByCallSingleRequest(phoneNumber)
                ).toWaitingTime()
            }
        }
    }

    suspend fun verifyOtpToken(phoneNumber: String, code: String): Either<LoginResponse> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                accountService.verifyOtpToken(
                    VerifyOtpTokenSingleRequest(phoneNumber, code)
                ).toLoginResponse()
            }
        }
    }

    suspend fun getUserInfo(): Either<UserInfo> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall {
                userInfoService?.getUserInfoRequest(checkoutToken)?.toAutoLoginUserInfo()
                    ?: UserInfo(phoneNumber = "")
            }
        }
    }

    fun getAccessToken(refreshToken: String): Either<String> {
        val authenticationRequestDto = GetAccessTokenSingleRequest(refreshToken)
        val response = accountService.getAccessToken(authenticationRequestDto).execute()

        return when {
            response.isSuccessful -> {
                val token = response.body()?.accessToken
                if (token != null) {
                    Either.Success(token)
                } else {
                    Either.Failure(ErrorModel.Error("token is empty"))
                }
            }

            response.code() == HttpURLConnection.HTTP_UNAUTHORIZED -> {
                Either.Failure(ErrorModel.AuthenticationError)
            }

            else -> {
                Either.Failure(ErrorModel.Error(response.message()))
            }
        }
    }
}
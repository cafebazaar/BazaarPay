package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.extensions.asNetworkException
import ir.cafebazaar.bazaarpay.extensions.getEitherFromResponse
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.WaitingTime
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.response.GetOtpTokenSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.request.GetOtpTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.request.GetOtpTokenByCallSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptokenbycall.response.GetOtpTokenByCallSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.request.GetAccessTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.refreshaccesstoken.response.GetAccessTokenSingleReply
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.request.VerifyOtpTokenSingleRequest
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.response.VerifyOtpTokenSingleReply
import ir.cafebazaar.bazaarpay.utils.Either
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.base.Base

internal class AccountRemoteDataSource {

    private val accountBase: Base = ServiceLocator.get(ServiceLocator.ACCOUNT)

    fun getOtpToken(phoneNumber: String): Either<WaitingTimeWithEnableCall> {
        try {
            accountBase.postMethod(
                GetOtpTokenSingleReply::class.java,
                GET_TOP_TOKEN_ENDPOINT
            ).apply {
                setPostBody(GetOtpTokenSingleRequest(phoneNumber))
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .singleReply
                            .getOtpTokenReply
                            .toWaitingTimeWithEnableCall()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    fun getOtpTokenByCall(phoneNumber: String): Either<WaitingTime> {
        try {
            accountBase.postMethod(
                GetOtpTokenByCallSingleReply::class.java,
                GET_OTP_TOKEN_BY_CALL_ENDPOINT
            ).apply {
                setPostBody(GetOtpTokenByCallSingleRequest(phoneNumber))
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .singleReply
                            .getOtpTokenReply.toWaitingTime()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    fun verifyOtpToken(phoneNumber: String, code: String): Either<LoginResponse> {
        try {
            accountBase.postMethod(
                VerifyOtpTokenSingleReply::class.java,
                VERIFY_TOP_TOKEN_ENDPOINT
            ).apply {
                setPostBody(VerifyOtpTokenSingleRequest(phoneNumber, code))
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .singleReply
                            .verifyOtpTokenReply.toLoginResponse()
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    fun getAccessToken(refreshToken: String): Either<String> {
        try {
            accountBase.postMethod(
                GetAccessTokenSingleReply::class.java,
                GET_ACCESS_TOKEN_ENDPOINT
            ).apply {
                setPostBody(GetAccessTokenSingleRequest(refreshToken))
                getResponse().also { response ->
                    return response.getEitherFromResponse {
                        response
                            .data
                            .singleReply
                            .getAccessTokenReply
                            .accessToken
                    }
                }
            }
        } catch (throwable: Throwable) {
            return Either.Failure(
                (throwable.cause ?: throwable).asNetworkException()
            )
        }
    }

    private companion object {
        const val GET_TOP_TOKEN_ENDPOINT = "rest-v1/process/GetOtpTokenRequest"
        const val GET_OTP_TOKEN_BY_CALL_ENDPOINT = "rest-v1/process/GetOtpTokenByCallRequest"
        const val VERIFY_TOP_TOKEN_ENDPOINT = "rest-v1/process/VerifyOtpTokenRequest"
        const val GET_ACCESS_TOKEN_ENDPOINT = "rest-v1/process/getAccessTokenRequest"
    }
}
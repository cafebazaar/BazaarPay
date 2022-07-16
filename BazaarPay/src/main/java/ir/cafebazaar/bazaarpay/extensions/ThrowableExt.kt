package ir.cafebazaar.bazaarpay.extensions

import android.accounts.NetworkErrorException
import com.google.gson.JsonSyntaxException
import ir.cafebazaar.bazaarpay.data.bazaar.models.*
import java.io.IOException

private const val MESSAGE_SERVER_ERROR = "Server Error"
private const val MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error"

fun Throwable.asNetworkException(): ErrorModel {
    return when (this) {
        is IOException -> {
            ErrorModel.NetworkConnection("No Network Connection", this)
        }
        is ResponseException -> {
            createHttpError(this)
        }
        is ErrorModel -> this
        else -> ErrorModel.Server(MESSAGE_SERVER_ERROR, this)
    }
}

private fun createHttpError(throwable: ResponseException): ErrorModel {
    return if (throwable == null) {
        ErrorModel.Server(
            MESSAGE_SERVER_ERROR,
            IllegalStateException("response.errorBody() is null")
        )
    } else {
        try {
            readErrorFromResponse(throwable.responseProperties)
        } catch (ignored: Exception) {
            ErrorModel.Server(MESSAGE_SERVER_ERROR, ignored)
        }
    }
}

private fun readErrorFromResponse(
    errorResponse: ResponseProperties?
): ErrorModel {
    return try {
        errorFromErrorResponse(errorResponse)
    } catch (ignored: JsonSyntaxException) {
        ErrorModel.Server(MESSAGE_SERVER_ERROR, ignored)
    } catch (ignored: IOException) {
        ErrorModel.Server(MESSAGE_SERVER_ERROR, ignored)
    } catch (ignored: IllegalStateException) {
        ErrorModel.Server(MESSAGE_SERVER_ERROR, ignored)
    } catch (ignored: ClassCastException) {
        ErrorModel.Server(MESSAGE_SERVER_ERROR, ignored)
    } catch (ignored: Exception) {
        ErrorModel.Server("Internal error during parsing error body", ignored)
    }
}
@Throws
private fun errorFromErrorResponse(
    errorResponse: ResponseProperties?
): ErrorModel {
    with(errorResponse) {
        return when (this?.errorCode) {
            ErrorCode.FORBIDDEN.value -> {
                ErrorModel.Forbidden(errorMessage)
            }
            ErrorCode.NOT_FOUND.value -> {
                ErrorModel.NotFound(errorMessage)
            }
            ErrorCode.RATE_LIMIT_EXCEEDED.value -> {
                ErrorModel.RateLimitExceeded(errorMessage)
            }
            ErrorCode.INTERNAL_SERVER_ERROR.value -> {
                ErrorModel.Server(
                    MESSAGE_INTERNAL_SERVER_ERROR,
                    NetworkErrorException(MESSAGE_INTERNAL_SERVER_ERROR)
                )
            }
            ErrorCode.LOGIN_IS_REQUIRED.value -> {
                ErrorModel.LoginIsRequired
            }
            else -> ErrorModel.Http(
                this?.errorMessage ?: "",
                this?.errorCode?.toErrorCode() ?: ErrorCode.UNKNOWN
            )
        }
    }
}

private fun Int.toErrorCode(): ErrorCode {
    return ErrorCode.values().firstOrNull { it.value == this } ?: ErrorCode.UNKNOWN
}
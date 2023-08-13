package ir.cafebazaar.bazaarpay.extensions

import android.accounts.NetworkErrorException
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorCode
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.models.BazaarErrorResponseDto
import ir.cafebazaar.bazaarpay.models.BazaarPayErrorResponseDto
import ir.cafebazaar.bazaarpay.models.BazaarPayErrorResponseDeserializer
import retrofit2.HttpException
import java.io.IOException

private const val MESSAGE_SERVER_ERROR = "Server Error"
private const val MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error"

fun asNetworkException(
    serviceType: ServiceType = ServiceType.BAZAAR,
    throwable: Throwable
): ErrorModel {
    return when (throwable) {
        is IOException -> {
            ErrorModel.NetworkConnection("No Network Connection", throwable)
        }

        is HttpException -> {
            createHttpError(serviceType, throwable)
        }

        is ErrorModel -> throwable
        else -> ErrorModel.Server(MESSAGE_SERVER_ERROR, throwable)
    }
}

private fun createHttpError(
    serviceType: ServiceType = ServiceType.BAZAAR,
    throwable: HttpException
): ErrorModel {
    val response = throwable.response()
    return if (response?.errorBody() == null) {
        ErrorModel.Server(
            MESSAGE_SERVER_ERROR,
            IllegalStateException("response.errorBody() is null")
        )
    } else {
        val errorBody = response.errorBody()!!.string()
        makeErrorModelFromNetworkResponse(errorBody, serviceType)
    }
}

internal fun makeErrorModelFromNetworkResponse(
    errorBody: String,
    serviceType: ServiceType = ServiceType.BAZAAR
): ErrorModel {
    return try {
        when (serviceType) {
            ServiceType.BAZAAR -> {
                val bazaarErrorResponse = GsonBuilder().create().fromJson(
                    errorBody,
                    BazaarErrorResponseDto::class.java
                )
                readErrorFromResponse(bazaarErrorResponse)
            }

            ServiceType.BAZAARPAY -> {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(
                    BazaarPayErrorResponseDto::class.java,
                    BazaarPayErrorResponseDeserializer()
                )
                val bazaarPayErrorResponse = gsonBuilder.create().fromJson(
                    errorBody,
                    BazaarPayErrorResponseDto::class.java
                )
                readErrorFromBazaarPayResponse(
                    bazaarPayErrorResponse,
                    errorBody
                )
            }
        }
    } catch (ignored: Exception) {
        ErrorModel.Server(MESSAGE_SERVER_ERROR, ignored)
    }
}

internal fun readErrorFromResponse(
    errorResponse: BazaarErrorResponseDto
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

private fun readErrorFromBazaarPayResponse(
    errorResponse: BazaarPayErrorResponseDto,
    errorJson: String? = null
): ErrorModel {
    return try {
        errorFromBazaarPayErrorResponse(errorResponse, errorJson)
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
    errorResponse: BazaarErrorResponseDto
): ErrorModel {
    with(errorResponse.properties) {
        return when (this?.errorCode) {
            ErrorCode.FORBIDDEN.value -> {
                ErrorModel.Forbidden(errorMessage)
            }

            ErrorCode.INPUT_NOT_VALID.value -> {
                ErrorModel.InputNotValid(errorMessage)
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

@Throws
private fun errorFromBazaarPayErrorResponse(
    errorResponse: BazaarPayErrorResponseDto,
    errorJson: String? = null
): ErrorModel {
    return ErrorModel.Http(
        message = errorResponse.detail,
        errorCode = ErrorCode.UNKNOWN,
        errorJson = errorJson
    )
}

private fun Int.toErrorCode(): ErrorCode {
    return ErrorCode.values().firstOrNull { it.value == this } ?: ErrorCode.UNKNOWN
}
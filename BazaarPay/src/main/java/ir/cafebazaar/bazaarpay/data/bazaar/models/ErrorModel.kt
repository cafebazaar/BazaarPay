package ir.cafebazaar.bazaarpay.data.bazaar.models

import android.content.Context
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.extensions.isNetworkAvailable
import java.io.Serializable

sealed class ErrorModel(override val message: String) : Throwable(message), Serializable {
    data class NetworkConnection(
        override val message: String,
        val throwable: Throwable
    ) : ErrorModel(message)

    data class Server(
        override val message: String,
        val throwable: Throwable
    ) : ErrorModel(message)

    data class Http(
        override val message: String,
        val errorCode: ErrorCode,
        val errorJson: String? = null
    ) : ErrorModel(message)

    data class NotFound(override val message: String) : ErrorModel(message)
    data class Forbidden(override val message: String) : ErrorModel(message)
    data class InputNotValid(override val message: String) : ErrorModel(message)
    data class RateLimitExceeded(override val message: String) : ErrorModel(message)
    data class Error(override val message: String) : ErrorModel(message)
    object LoginIsRequired : ErrorModel("Login is Required")
    object UnExpected : ErrorModel("unexpected error")
    object AuthenticationError : ErrorModel("Authentication")

    abstract class Feature(message: String) : ErrorModel(message)

    fun getErrorIcon(context: Context): Int = when (this) {
        is NetworkConnection -> {
            if (context.isNetworkAvailable().not()) {
                R.drawable.ic_signal_wifi_off_icon_primary_24dp_old
            } else {
                R.drawable.ic_error_outline_icon_primary_24dp_old
            }
        }
        else -> {
            R.drawable.ic_error_outline_icon_primary_24dp_old
        }
    }
}

enum class ErrorCode(val value: Int) {
    FORBIDDEN(403),
    INPUT_NOT_VALID(400),
    NOT_FOUND(404),
    RATE_LIMIT_EXCEEDED(429),
    INTERNAL_SERVER_ERROR(500),
    LOGIN_IS_REQUIRED(401),
    UNKNOWN(0)
}

internal class InvalidPhoneNumberException : ErrorModel.Feature("Invalid phone number")
package ir.cafebazaar.bazaarpay.data.bazaar.models

sealed class ErrorModel(override val message: String) : Throwable(message) {
    data class NetworkConnection(
        override val message: String,
        val throwable: Throwable
    ) : ErrorModel(message)

    data class Server(
        override val message: String,
        val throwable: Throwable
    ) : ErrorModel(message)

    data class Http(override val message: String, val errorCode: ErrorCode) : ErrorModel(message)
    data class NotFound(override val message: String) : ErrorModel(message)
    data class Forbidden(override val message: String) : ErrorModel(message)
    data class RateLimitExceeded(override val message: String) : ErrorModel(message)
    data class Error(override val message: String) : ErrorModel(message)
    object LoginIsRequired : ErrorModel("Login is Required")
    object UnExpected : ErrorModel("unexpected error")
    object AuthenticationError : ErrorModel("Authentication")

    abstract class Feature(message: String) : ErrorModel(message)
}

enum class ErrorCode(val value: Int) {
    FORBIDDEN(403),
    NOT_FOUND(404),
    RATE_LIMIT_EXCEEDED(429),
    INTERNAL_SERVER_ERROR(500),
    LOGIN_IS_REQUIRED(401),
    UNKNOWN(0)
}

internal class InvalidPhoneNumberException : ErrorModel.Feature("Invalid phone number")
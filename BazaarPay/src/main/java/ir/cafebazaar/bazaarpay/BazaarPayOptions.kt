package ir.cafebazaar.bazaarpay

import androidx.core.net.toUri

/**
 * The mandatory and optional parameters to configure the payment flow.
 *
 * @property checkoutToken the unique identifier that provides essential payment information.
 * @property isInDarkMode enables *Dark Mode* for the UI elements of the payment flow, which are in *Light Mode* by default.
 * @property phoneNumber the default phone number to pre-fill the login screen's input field. It uses a `null` value by default, resulting in no pre-filled input.
 */
class BazaarPayOptions private constructor(
    val checkoutToken: String,
    val isInDarkMode: Boolean? = null,
    val phoneNumber: String? = null,
    val autoLoginPhoneNumber: String? = null,
    val isAutoLoginEnable: Boolean = false,
) {

    @Deprecated(
        "BazaarPayOptions constructor is deprecated, Please use the Builder",
        replaceWith = ReplaceWith("BazaarPayOptions.Builder.checkoutToken(checkoutToken).phoneNumber(phoneNumber).build()"),
        level = DeprecationLevel.WARNING
    )
    constructor(
        checkoutToken: String,
        phoneNumber: String? = null,
    ) : this(checkoutToken = checkoutToken, isInDarkMode = null, phoneNumber = phoneNumber)

    companion object Builder {

        private var checkoutToken: String = ""
        private var phoneNumber: String? = null
        private var paymentUrlParser: PaymentURLParser? = null

        fun checkoutToken(checkoutToken: String) = apply { this.checkoutToken = checkoutToken }

        fun phoneNumber(phoneNumber: String?) = apply { this.phoneNumber = phoneNumber }

        fun paymentUrl(paymentUrl: String) = apply {
            this.paymentUrlParser = PaymentURLParser(paymentUrl)
        }

        fun build() = BazaarPayOptions(
            checkoutToken = paymentUrlParser?.getCheckoutToken() ?: checkoutToken,
            phoneNumber = phoneNumber,
            autoLoginPhoneNumber = paymentUrlParser?.getAutoLoginPhoneNumber(),
            isAutoLoginEnable = paymentUrlParser?.isAutoLoginEnable() ?: false
        )
    }
}

private class PaymentURLParser(val paymentUrl: String) {
    companion object {

        const val CHECKOUT_TOKEN = "token"
        const val AUTO_LOGIN = "can_request_without_login"
        const val AUTO_LOGIN_PHONE_NUMBER = "phone"
    }

    fun getCheckoutToken(): String? {
        return paymentUrl.toUri().getQueryParameter(CHECKOUT_TOKEN)
    }

    fun getAutoLoginPhoneNumber(): String? {
        return paymentUrl.toUri().getQueryParameter(AUTO_LOGIN_PHONE_NUMBER).takeIf {
            isAutoLoginEnable()
        }
    }

    fun isAutoLoginEnable(): Boolean {
        return paymentUrl.toUri().getBooleanQueryParameter(AUTO_LOGIN, false)
    }
}

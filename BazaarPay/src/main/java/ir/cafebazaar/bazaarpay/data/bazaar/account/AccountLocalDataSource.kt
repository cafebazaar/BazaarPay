package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.DataSourceValueHolder
import ir.cafebazaar.bazaarpay.data.SharedDataSource

internal class AccountLocalDataSource {

    private val accountSharedDataSource: SharedDataSource = ServiceLocator.get(ServiceLocator.ACCOUNT)

    var accessToken: String by DataSourceValueHolder(
        accountSharedDataSource,
        ACCESS_TOKEN,
        rejectValue = ""
    )

    var refreshToken: String by DataSourceValueHolder(
        accountSharedDataSource,
        REFRESH_TOKEN,
        rejectValue = ""
    )

    var accessTokenTimestamp: Long by DataSourceValueHolder(
        accountSharedDataSource,
        ACCESS_TOKEN_TIMESTAMP,
        rejectValue = 0
    )

    private var autoFillPhones: String by DataSourceValueHolder(
        accountSharedDataSource,
        KEY_AUTO_FILL_PHONES,
        rejectValue = ""
    )

    fun getAutoFillPhones(): List<String> {
        return autoFillPhones.split(JOIN_STRING_SEPARATOR)
    }

    fun putAutoFillPhones(phones: String) {
        getAutoFillPhones()
            .filter { it.isNotEmpty() }
            .toMutableSet()
            .apply { add(phones) }
            .toList()
            .also { newPhoneNumberList ->
                autoFillPhones = newPhoneNumberList.joinToString(JOIN_STRING_SEPARATOR)
            }

    }

    fun removeAccessToken() {
        accountSharedDataSource.remove(ACCESS_TOKEN, commit = true)
    }

    fun removeRefreshToken() {
        accountSharedDataSource.remove(REFRESH_TOKEN, commit = true)
    }

    private companion object {

        const val REFRESH_TOKEN = "refresh_token"
        const val ACCESS_TOKEN = "access_token"
        const val ACCESS_TOKEN_TIMESTAMP = "access_token_timestamp"
        const val KEY_AUTO_FILL_PHONES = "auto_fill_phones"
        const val JOIN_STRING_SEPARATOR = ","
    }
}
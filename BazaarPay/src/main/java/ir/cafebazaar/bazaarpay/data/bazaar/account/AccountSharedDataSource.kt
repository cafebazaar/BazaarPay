package ir.cafebazaar.bazaarpay.data.bazaar.account

import ir.cafebazaar.bazaarpay.data.SharedDataSource

internal class AccountSharedDataSource: SharedDataSource() {

    override val fileName: String = ACCOUNT_DATA_SOURCE_FILE_NAME

    private companion object {
        const val ACCOUNT_DATA_SOURCE_FILE_NAME = "Account"
    }
}
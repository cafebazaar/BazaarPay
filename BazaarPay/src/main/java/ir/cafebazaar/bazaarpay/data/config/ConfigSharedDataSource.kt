package ir.cafebazaar.bazaarpay.data.config

import ir.cafebazaar.bazaarpay.data.SharedDataSource

internal class ConfigSharedDataSource: SharedDataSource() {

    override val fileName: String = CONFIG_DATA_SOURCE_FILE_NAME

    private companion object {
        const val CONFIG_DATA_SOURCE_FILE_NAME = "Config"
    }
}
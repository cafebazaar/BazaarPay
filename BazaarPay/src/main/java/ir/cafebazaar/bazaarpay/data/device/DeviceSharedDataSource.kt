package ir.cafebazaar.bazaarpay.data.device

import ir.cafebazaar.bazaarpay.data.SharedDataSource

internal class DeviceSharedDataSource: SharedDataSource() {

    override val fileName: String = DEVICE_DATA_SOURCE_FILE_NAME

    private companion object {
        const val DEVICE_DATA_SOURCE_FILE_NAME = "Device"
    }
}
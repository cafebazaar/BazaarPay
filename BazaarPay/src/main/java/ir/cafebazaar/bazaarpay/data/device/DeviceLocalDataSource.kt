package ir.cafebazaar.bazaarpay.data.device

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.DataSourceValueHolder
import ir.cafebazaar.bazaarpay.data.SharedDataSource

internal class DeviceLocalDataSource {

    private val deviceSharedDataSource: SharedDataSource = ServiceLocator.get(ServiceLocator.DEVICE)

    var clientId: String by DataSourceValueHolder(
        deviceSharedDataSource,
        CLIENT_ID,
        rejectValue = ""
    )

    private companion object {

        const val CLIENT_ID = "client_id"
    }
}
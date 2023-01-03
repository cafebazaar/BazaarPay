package ir.cafebazaar.bazaarpay.data.device

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import java.util.*

internal class DeviceRepository {

    private val deviceLocalDataSource: DeviceLocalDataSource = ServiceLocator.get()

    fun getClientId(): String {
        return deviceLocalDataSource.clientId.ifEmpty {
            UUID.randomUUID().toString().also {
                deviceLocalDataSource.clientId = it
            }
        }
    }
}
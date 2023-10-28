package ir.cafebazaar.bazaarpay.data.analytics

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.analytics.model.toActionLogDto
import ir.cafebazaar.bazaarpay.data.analytics.model.ActionLogRequestDto
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountLocalDataSource
import ir.cafebazaar.bazaarpay.data.device.DeviceRepository
import ir.cafebazaar.bazaarpay.utils.doOnSuccess

internal class AnalyticsRepository {

    private val analyticsRemoteDataSource: AnalyticsRemoteDataSource = ServiceLocator.get()
    private val deviceRepository: DeviceRepository = ServiceLocator.get()
    private val accountLocalDataSource: AccountLocalDataSource = ServiceLocator.get()

    suspend fun sendAnalyticsEvents() {
        val phone = accountLocalDataSource.loginPhone
        val actionLogs = Analytics.getPendingActionLogs().also {
            if (it.isEmpty()) return
        }.map {
            it.toActionLogDto(
                source = ANDROID_SDK_SOURCE,
                deviceId = deviceRepository.getClientId(),
                phone = phone
            )
        }
        analyticsRemoteDataSource.sendEventsToServer(ActionLogRequestDto(actionLogs)).doOnSuccess {
            Analytics.onSyncedActionLogs(actionLogs.lastOrNull()?.id ?: -1)
        }
    }

    companion object {

        private const val ANDROID_SDK_SOURCE = "ANDROID_SDK"
    }
}
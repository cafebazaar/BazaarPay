package ir.cafebazaar.bazaarpay.data.analytics

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.analytics.model.toActionLogDto
import ir.cafebazaar.bazaarpay.data.analytics.model.ActionLogRequestDto
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.device.DeviceRepository
import ir.cafebazaar.bazaarpay.utils.doOnSuccess

internal class AnalyticsRepository {

    private val analyticsRemoteDataSource: AnalyticsRemoteDataSource = ServiceLocator.get()
    private val deviceRepository: DeviceRepository = ServiceLocator.get()
    private val accountRepository: AccountRepository = ServiceLocator.get()

    suspend fun sendAnalyticsEvents() {
        val actionLogs = Analytics.getPendingActionLogs().also {
            if (it.isEmpty()) return
        }.map {
            it.toActionLogDto(
                source = ANDROID_SDK_SOURCE,
                accountId = accountRepository.getAccountId(),
                deviceId = deviceRepository.getClientId()
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
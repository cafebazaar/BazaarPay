package ir.cafebazaar.bazaarpay.data.analytics

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.analytics.model.toActionLogDto
import ir.cafebazaar.bazaarpay.data.analytics.model.ActionLogRequestDto
import ir.cafebazaar.bazaarpay.utils.doOnSuccess
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class AnalyticsRepository {

    private val analyticsRemoteDataSource: AnalyticsRemoteDataSource = ServiceLocator.get()

    //todo (fix accountId and deviceId)
    fun sendAnalyticsEvents() = GlobalScope.launch {
        val actionLogs = Analytics.getPendingActionLogs().also {
            if (it.isEmpty()) return@launch
        }.map {
            it.toActionLogDto(
                source = ANDROID_SDK_SOURCE,
                accountId = "",
                deviceId = ""
            )
        }
        analyticsRemoteDataSource.sendEventsToServer(ActionLogRequestDto(actionLogs)).doOnSuccess {
            Analytics.onSyncedActionLogs(actionLogs.lastOrNull()?.id ?: -1)
        }
    }

    companion object {

        private const val ANDROID_SDK_SOURCE = 1
    }
}
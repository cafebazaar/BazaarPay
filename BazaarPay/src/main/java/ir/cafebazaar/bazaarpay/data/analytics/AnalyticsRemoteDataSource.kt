package ir.cafebazaar.bazaarpay.data.analytics

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.analytics.api.AnalyticsService
import ir.cafebazaar.bazaarpay.data.analytics.model.ActionLogRequestDto
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.utils.Either

internal class AnalyticsRemoteDataSource {

    private val analyticsService: AnalyticsService by lazy {
        ServiceLocator.get()
    }

    suspend fun sendEventsToServer(actionLogRequestDto: ActionLogRequestDto): Either<Unit> {
        return safeApiCall {
            analyticsService.sendEvents(actionLogRequestDto)
        }
    }
}
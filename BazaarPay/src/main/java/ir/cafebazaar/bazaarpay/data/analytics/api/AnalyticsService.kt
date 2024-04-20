package ir.cafebazaar.bazaarpay.data.analytics.api

import ir.cafebazaar.bazaarpay.data.analytics.model.ActionLogRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AnalyticsService {

    @POST("analytics/action-log/")
    suspend fun sendEvents(
        @Body actionLogRequestDto: ActionLogRequestDto
    )
}
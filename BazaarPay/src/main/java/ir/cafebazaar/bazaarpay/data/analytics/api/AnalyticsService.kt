package ir.cafebazaar.bazaarpay.data.analytics.api

import ir.cafebazaar.bazaarpay.data.analytics.model.ActionLogRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AnalyticsService {

    @POST("GetOtpTokenRequest")
    suspend fun sendEvents(
        @Body actionLogRequestDto: ActionLogRequestDto
    ): Unit
}
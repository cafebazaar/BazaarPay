package ir.cafebazaar.bazaarpay.data.config.api

import ir.cafebazaar.bazaarpay.data.config.response.ConfigResponseDto
import retrofit2.http.GET

internal interface ConfigService {

    @GET("config/")
    suspend fun getConfig(): ConfigResponseDto
}
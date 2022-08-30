package ir.cafebazaar.bazaarpay.network.dynamicrestclient.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.*
import okhttp3.internal.platform.Platform
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class RestClient {

    fun createRetrofitClient(
        httpClient: OkHttpClient,
        baseUrl: String,
        converterFactories: List<Converter.Factory>? = null
    ): Retrofit {
        println("@@@@@@@ 1")
        return Retrofit.Builder().apply {
            baseUrl(baseUrl)
            client(httpClient)
            converterFactories?.forEach { factory ->
                println("@@@@@@@ 2")
                addConverterFactory(factory)
            } ?: run {
                println("@@@@@@@ 3")
                addConverterFactory(GsonConverterFactory.create(provideGson()))
            }
        }.build()
    }

    fun getOkHttp(
        tokenAuthenticator: Authenticator? = null,
        interceptors: List<Interceptor>? = null,
        cache: Cache? = null,
        debugMode: Boolean = false
    ): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        if (debugMode) {
            LoggingInterceptor.Builder().apply {
                setLevel(Level.BASIC)
                log(Platform.INFO)
                request(TAG)
                response(TAG)
            }.build().also {
                httpClientBuilder.addInterceptor(it)
            }
        }
        interceptors?.let {
            for (interceptor in interceptors) {
                httpClientBuilder.addInterceptor(interceptor)
            }
        }
        tokenAuthenticator?.let { httpClientBuilder.authenticator(it) }
        cache?.let { httpClientBuilder.cache(it) }
        httpClientBuilder.connectTimeout(
            NETWORK_TIMEOUT,
            TimeUnit.SECONDS
        )
        httpClientBuilder.readTimeout(
            NETWORK_TIMEOUT,
            TimeUnit.SECONDS
        )
        return httpClientBuilder.build()
    }

    companion object {

        private const val NETWORK_TIMEOUT: Long = 15
        private const val TAG: String = "ClientRequest"

        fun provideGson(): Gson {
            return GsonBuilder().setLenient().create()
        }
    }
}
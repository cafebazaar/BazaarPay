package ir.cafebazaar.bazaarpay.network.dynamicrestclient.client

import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Interceptor
import retrofit2.Converter

interface ClientBuilder {

    fun withAuthenticator(authenticator: Authenticator): ClientBuilder

    fun withInterceptors(interceptors: List<Interceptor>): ClientBuilder

    fun withInterceptor(interceptor: Interceptor): ClientBuilder

    fun withConverterFactories(converterFactories: List<Converter.Factory>): ClientBuilder

    fun withConverterFactory(converterFactory: Converter.Factory): ClientBuilder

    fun withDebugMode(debugMode: Boolean): ClientBuilder

    fun withCache(cache: Cache): ClientBuilder

    fun build(): Client
}
package ir.cafebazaar.bazaarpay.data.device

import ir.cafebazaar.bazaarpay.ServiceLocator
import okhttp3.Interceptor
import okhttp3.Response

internal class DeviceInterceptor: Interceptor {

    private val deviceRepository: DeviceRepository = ServiceLocator.get()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val clientId = deviceRepository.getClientId()

        val requestBuilder = originalRequest.newBuilder()
            .header(CLIENT_ID_KEY, clientId)
            .method(originalRequest.method, originalRequest.body)

        return chain.proceed(requestBuilder.build())
    }

    companion object {

        const val CLIENT_ID_KEY = "Client-Id"
    }
}
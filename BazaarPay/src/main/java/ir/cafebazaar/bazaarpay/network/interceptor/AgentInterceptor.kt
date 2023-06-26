package ir.cafebazaar.bazaarpay.network.interceptor

import android.content.Context
import ir.cafebazaar.bazaarpay.BuildConfig
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.utils.getAppVersionName
import okhttp3.Interceptor
import okhttp3.Response

object AgentInterceptor : Interceptor {

    private const val USER_AGENT_HEADER_TITLE: String = "UserAgent"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader(USER_AGENT_HEADER_TITLE, buildUserAgentHeaderValue())
                .build()
        return chain.proceed(request)
    }

    private fun buildUserAgentHeaderValue(): String {
        return "BazaarPayAndroidSDK/" +
                "${BuildConfig.VERSION}/" +
                "${ServiceLocator.get<Context>().packageName}/" +
                getAppVersionName()
    }
}
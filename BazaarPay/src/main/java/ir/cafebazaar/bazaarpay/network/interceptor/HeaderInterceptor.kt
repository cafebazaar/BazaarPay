package ir.cafebazaar.bazaarpay.network.interceptor

import android.content.Context
import ir.cafebazaar.bazaarpay.BuildConfig
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.utils.getAppVersionName
import okhttp3.Interceptor
import okhttp3.Response

object HeaderInterceptor : Interceptor {

    private const val USER_AGENT_HEADER_TITLE: String = "UserAgent"
    private const val ACTION_LOG_TRACE_ID: String = "x-action-log-trace-id"

    private val userAgent by lazy { buildUserAgentHeaderValue() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader(USER_AGENT_HEADER_TITLE, userAgent)
                .header(ACTION_LOG_TRACE_ID, Analytics.getSessionId())
                .build()
        return chain.proceed(request)
    }

    private fun buildUserAgentHeaderValue(): String {
        return "BazaarPayAndroidSDK/" +
                "${BuildConfig.VERSION}/" +
                "${ServiceLocator.getOrNull<Context>()?.packageName}/" +
                getAppVersionName()
    }
}
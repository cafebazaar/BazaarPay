package ir.cafebazaar.bazaarpay.screens.payment.webpage

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.data.config.ConfigRepository
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import kotlinx.coroutines.launch

internal class WebPageViewModel : ViewModel() {

    private val configRepository: ConfigRepository = ServiceLocator.get()
    private val globalDispatchers: GlobalDispatchers = ServiceLocator.get()

    private var prevUrl: String? = null

    fun onError(
        failingUrl: String,
        errorCode: Int,
        description: String?,
    ) {
        sendError(
            failingUrl = failingUrl,
            errorCode = errorCode,
            description = description,
            errorType = ERROR_TYPE_RESOURCE,
        )
    }

    fun onSslError(
        failingUrl: String,
        errorCode: Int,
        description: String?,
    ) {
        sendError(
            failingUrl = failingUrl,
            errorCode = errorCode,
            description = description,
            errorType = ERROR_TYPE_SSL,
        )
    }

    fun onHttpError(
        failingUrl: String,
        errorCode: Int,
        description: String?,
    ) {
        configRepository.getConfig()?.actionLog?.validators?.let { validators ->
            viewModelScope.launch(globalDispatchers.iO) {
                validators.forEach { validator ->
                    if (failingUrl.matches(validator.toRegex())) {
                        sendError(
                            failingUrl = failingUrl,
                            errorCode = errorCode,
                            description = description,
                            errorType = ERROR_TYPE_HTTP,
                        )
                        return@forEach
                    }
                }
            }
        }
    }

    fun onPageStarted(url: String) {
        closePreviousLoadedUrl(newUrl = url)
        Analytics.sendLoadEvent(
            where = getWhere(url),
            extra = hashMapOf(
                "url" to url,
                "page_finished" to false,
            ),
        )
    }

    fun onDestroy() {
        closePreviousLoadedUrl(newUrl = null)
    }

    fun onPageFinished(url: String) {
        Analytics.sendLoadEvent(
            where = getWhere(url),
            extra = hashMapOf(
                "url" to url,
                "page_finished" to true,
            ),
        )
    }

    private fun closePreviousLoadedUrl(newUrl: String?) {
        if (prevUrl != null) {
            Analytics.sendCloseEvent(
                where = getWhere(requireNotNull(prevUrl)),
            )
        }
        prevUrl = newUrl
    }

    private fun sendError(
        failingUrl: String,
        errorCode: Int,
        description: String?,
        errorType: String,
    ) {
        Analytics.sendErrorEvent(
            where = getWhere(failingUrl),
            extra = hashMapOf(
                "url" to failingUrl,
                "code" to errorCode,
                "type" to errorType,
                "description" to description.orEmpty(),
            ),
        )
    }

    private fun getWhere(url: String): String {
        val uri = Uri.parse(url)
        return "URL:" + uri.buildUpon().clearQuery().build().toString()
    }

    private companion object {

        const val ERROR_TYPE_RESOURCE = "resource"
        const val ERROR_TYPE_SSL = "ssl"
        const val ERROR_TYPE_HTTP = "http"
    }
}
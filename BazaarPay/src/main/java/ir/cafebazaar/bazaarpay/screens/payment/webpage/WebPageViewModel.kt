package ir.cafebazaar.bazaarpay.screens.payment.webpage

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

    fun onPageStarted(url: String) {
        Analytics.sendLoadEvent(
            where = "URL:$url",
            extra = hashMapOf("page_finished" to false),
        )
    }

    fun onPageFinished(url: String) {
        Analytics.sendLoadEvent(
            where = "URL:$url",
            extra = hashMapOf("page_finished" to true),
        )
    }

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

    private fun sendError(
        failingUrl: String,
        errorCode: Int,
        description: String?,
        errorType: String,
    ) {
        Analytics.sendErrorEvent(
            where = "URL:$failingUrl",
            extra = hashMapOf(
                "code" to errorCode,
                "type" to errorType,
                "description" to description.orEmpty(),
            ),
        )
    }

    private companion object {

        const val ERROR_TYPE_RESOURCE = "resource"
        const val ERROR_TYPE_SSL = "ssl"
        const val ERROR_TYPE_HTTP = "http"
    }
}
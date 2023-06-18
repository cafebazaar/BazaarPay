package ir.cafebazaar.bazaarpay.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.data.analytics.AnalyticsRepository
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

internal class AnalyticsViewModel : ViewModel() {

    private val analyticsRepository: AnalyticsRepository? = ServiceLocator.getOrNull()

    fun listenThreshold() = viewModelScope.launch {
        Analytics.actionLogsThresholdFlow.debounce(1000).collect {
            analyticsRepository?.sendAnalyticsEvents()
        }
    }

    override fun onCleared() {
        analyticsRepository?.sendAnalyticsEvents()
        Analytics.shutDownAnalytics()
        super.onCleared()
    }
}
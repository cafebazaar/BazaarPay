package ir.cafebazaar.bazaarpay.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.data.analytics.AnalyticsRepository
import kotlinx.coroutines.launch

internal class AnalyticsViewModel : ViewModel() {

    private val analyticsRepository by lazy { ServiceLocator.get<AnalyticsRepository>() }

    fun listenThreshold() = viewModelScope.launch {
        Analytics.actionLogsThresholdFlow.collect {
            analyticsRepository.sendAnalyticsEvents()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Analytics.shutDownAnalytics()
    }
}
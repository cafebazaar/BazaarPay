package ir.cafebazaar.bazaarpay.analytics

import android.util.Log
import com.google.gson.Gson
import ir.cafebazaar.bazaarpay.BuildConfig
import ir.cafebazaar.bazaarpay.analytics.model.ActionLog
import ir.cafebazaar.bazaarpay.analytics.model.EventType
import java.util.concurrent.atomic.AtomicLong

internal object Analytics {

    private const val TAG = "Analytics"

    private val actionLogs = mutableListOf<ActionLog>()

    private val id = AtomicLong(0)
    private var lastSyncedId: Long? = null

    private const val ANDROID_SDK_SOURCE = 1

    @Synchronized
    fun sendClickEvent(where: String, extra: HashMap<String, Any> = hashMapOf()) {
        addAnalyticsEvent(EventType.CLICK, where, extra)
    }

    @Synchronized
    fun sendSwipeEvent(where: String, extra: HashMap<String, Any> = hashMapOf()) {
        addAnalyticsEvent(EventType.SWIPE, where, extra)
    }

    @Synchronized
    fun sendCloseEvent(where: String, extra: HashMap<String, Any> = hashMapOf()) {
        addAnalyticsEvent(EventType.CLOSE, where, extra)
    }

    @Synchronized
    fun sendVisitEvent(where: String, extra: HashMap<String, Any> = hashMapOf()) {
        addAnalyticsEvent(EventType.VISIT, where, extra)
    }

    @Synchronized
    private fun addAnalyticsEvent(type: EventType, where: String, extra: HashMap<String, Any>) {
        actionLogs.removeAll { it.id <= (lastSyncedId ?: -1L) }
        val now = System.currentTimeMillis()
        val gson = Gson()
        val extraInStringFormat = gson.toJson(extra).toString()
        val actionLog = ActionLog(
            id = id.incrementAndGet(),
            type = type,
            timestamp = now,
            where = where,
            extra = extraInStringFormat,
        )
        actionLogs.add(actionLog)

        if (BuildConfig.DEBUG) {
            Log.i(TAG, actionLog.toString())
        }
    }

    @Synchronized
    internal fun getPendingActionLogs(): List<ActionLog> {
        return actionLogs.filter { it.id > (lastSyncedId ?: -1) }
    }

    internal fun onSyncedActionLogs(lastSyncedItem: Long) {
        lastSyncedId = lastSyncedItem
    }
}
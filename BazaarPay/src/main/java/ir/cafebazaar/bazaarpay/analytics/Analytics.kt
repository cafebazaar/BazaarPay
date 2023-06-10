package ir.cafebazaar.bazaarpay.analytics

import android.util.Log
import com.google.gson.Gson
import ir.cafebazaar.bazaarpay.BuildConfig
import ir.cafebazaar.bazaarpay.analytics.model.ActionLog
import ir.cafebazaar.bazaarpay.analytics.model.EventType
import ir.cafebazaar.bazaarpay.analytics.model.PaymentFlowDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

internal object Analytics {

    private const val TAG = "Analytics"

    private val actionLogs = mutableListOf<ActionLog>()

    private val id = AtomicLong(0)
    private var lastSyncedId: Long? = null

    private var checkOutToken: String? = null
    private var merchantName: String? = null
    private var amount: String? = null

    private const val ACTION_LOG_THRESHOLD = 20
    private val actionLogsThreshold = MutableStateFlow(Unit)
    val actionLogsThresholdFlow: StateFlow<Unit> = actionLogsThreshold

    @Volatile
    private var SESSION_ID: String? = null

    internal fun getSessionId(): String {
        return SESSION_ID ?: synchronized(this) {
            val uuID = UUID.randomUUID().toString()
            SESSION_ID = uuID
            uuID
        }
    }

    fun setCheckOutToken(checkOutToken: String) {
        this.checkOutToken = checkOutToken
    }

    fun setMerchantName(merchantName: String) {
        this.merchantName = merchantName
    }

    fun setAmount(amount: String) {
        this.amount = amount
    }

    @Synchronized
    fun sendClickEvent(
        where: String,
        extra: HashMap<String, Any> = hashMapOf(),
        pageDetails: HashMap<String, Any> = hashMapOf(),
    ) {
        addAnalyticsEvent(EventType.CLICK, where, extra, pageDetails)
    }

    @Synchronized
    fun sendSwipeEvent(
        where: String,
        extra: HashMap<String, Any> = hashMapOf(),
        pageDetails: HashMap<String, Any> = hashMapOf(),
    ) {
        addAnalyticsEvent(EventType.SWIPE, where, extra, pageDetails)
    }

    @Synchronized
    fun sendCloseEvent(
        where: String,
        extra: HashMap<String, Any> = hashMapOf(),
        pageDetails: HashMap<String, Any> = hashMapOf(),
    ) {
        addAnalyticsEvent(EventType.CLOSE, where, extra, pageDetails)
    }

    @Synchronized
    fun sendVisitEvent(
        where: String,
        extra: HashMap<String, Any> = hashMapOf(),
        pageDetails: HashMap<String, Any> = hashMapOf(),
    ) {
        addAnalyticsEvent(EventType.VISIT, where, extra, pageDetails)
    }

    @Synchronized
    private fun addAnalyticsEvent(
        type: EventType,
        where: String,
        extra: HashMap<String, Any>,
        pageDetails: HashMap<String, Any>,
    ) {

        actionLogs.takeIf { (it.firstOrNull()?.id ?: 0) <= (lastSyncedId ?: -1L) }
            ?.removeAll { it.id <= (lastSyncedId ?: -1L) }

        checkActionLogThreshold()

        val now = System.currentTimeMillis()
        val gson = Gson()
        val extraInStringFormat = gson.toJson(extra).toString()
        val pageDetailsInStringFormat = gson.toJson(pageDetails).toString()
        val actionLog = ActionLog(
            id = id.incrementAndGet(),
            sessionId = getSessionId(),
            type = type,
            timestamp = now,
            where = where,
            pageDetails = pageDetailsInStringFormat,
            extra = extraInStringFormat,
            paymentFlowDetails = PaymentFlowDetails(
                checkOutToken = checkOutToken,
                merchantName = merchantName,
                amount = amount
            )
        )
        actionLogs.add(actionLog)

        if (BuildConfig.DEBUG) {
            Log.i(TAG, actionLog.toString())
        }
    }

    private fun checkActionLogThreshold() {
        if (actionLogs.size > ACTION_LOG_THRESHOLD) {
            actionLogsThreshold.value = Unit
        }
    }

    @Synchronized
    internal fun getPendingActionLogs(): List<ActionLog> {
        return actionLogs.filter { it.id > (lastSyncedId ?: -1) }
    }

    internal fun onSyncedActionLogs(lastSyncedItem: Long) {
        lastSyncedId = lastSyncedItem
    }

    // this is required to generate a new session id per each session
    internal fun shutDownAnalytics() {
        SESSION_ID = null
    }
}
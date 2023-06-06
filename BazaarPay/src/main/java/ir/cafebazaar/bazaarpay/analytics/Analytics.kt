package ir.cafebazaar.bazaarpay.analytics

import android.util.Log
import com.google.gson.Gson
import ir.cafebazaar.bazaarpay.BuildConfig
import ir.cafebazaar.bazaarpay.analytics.model.ActionLog
import ir.cafebazaar.bazaarpay.analytics.model.EventType
import ir.cafebazaar.bazaarpay.analytics.model.PaymentFlowDetails
import java.util.concurrent.atomic.AtomicLong

internal object Analytics {

    private const val TAG = "Analytics"

    private val actionLogs = mutableListOf<ActionLog>()

    private val id = AtomicLong(0)
    private var lastSyncedId: Long? = null

    private var checkOutToken: String? = null
    private var merchantName: String? = null
    private var amount: String? = null

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
        actionLogs.removeAll { it.id <= (lastSyncedId ?: -1L) }
        val now = System.currentTimeMillis()
        val gson = Gson()
        val extraInStringFormat = gson.toJson(extra).toString()
        val pageDetailsInStringFormat = gson.toJson(pageDetails).toString()
        val actionLog = ActionLog(
            id = id.incrementAndGet(),
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

    @Synchronized
    internal fun getPendingActionLogs(): List<ActionLog> {
        return actionLogs.filter { it.id > (lastSyncedId ?: -1) }
    }

    internal fun onSyncedActionLogs(lastSyncedItem: Long) {
        lastSyncedId = lastSyncedItem
    }
}
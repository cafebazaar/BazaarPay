package ir.cafebazaar.bazaarpay.analytics.plugins

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.analytics.model.EventType
import ir.cafebazaar.bazaarpay.base.ActivityPlugin
import ir.cafebazaar.bazaarpay.base.FragmentPlugin
import ir.cafebazaar.bazaarpay.utils.StopWatch

internal class CloseEventPlugin(
    private val where: String,
    private var activity: Activity?
) : ActivityPlugin, FragmentPlugin {

    private val elapsedTimeStopWatch by lazy { StopWatch() }

    override fun onResume(owner: LifecycleOwner) {
        elapsedTimeStopWatch.start()
    }

    override fun onPause(owner: LifecycleOwner) {
        Analytics.sendCloseEvent(
            where = where,
            extra = hashMapOf(ELAPSED_TIME to getElapsedTime())
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (shouldLogDestructionEvent()) {
            Analytics.sendCloseEvent(
                where = where,
                extra = hashMapOf(EventType.CLOSE.toString() to DESTRUCTION_EVENT)
            )
        }
        activity = null
    }

    private fun shouldLogDestructionEvent(): Boolean {
        return (activity?.isDestroyed == false) ||
                (activity?.isDestroyed == true && activity?.isFinishing == true)
    }

    private fun getElapsedTime(): Long {
        elapsedTimeStopWatch.stop()
        return elapsedTimeStopWatch.getElapsedTimeMillis()
    }

    private companion object {

        const val DESTRUCTION_EVENT = "page_destruction"
        const val ELAPSED_TIME = "elapsed_time"
    }
}
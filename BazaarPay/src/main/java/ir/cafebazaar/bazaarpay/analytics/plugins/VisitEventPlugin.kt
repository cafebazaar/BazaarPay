package ir.cafebazaar.bazaarpay.analytics.plugins

import androidx.lifecycle.LifecycleOwner
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.base.ActivityPlugin
import ir.cafebazaar.bazaarpay.base.FragmentPlugin

internal class VisitEventPlugin(
    private val where: String
) : ActivityPlugin, FragmentPlugin {

    override fun onCreate(owner: LifecycleOwner) {
        Analytics.sendVisitEvent(
            where = where,
            extra = hashMapOf(VISIT_TYPE to CREATION_EVENT)
        )
    }

    override fun onResume(owner: LifecycleOwner) {
        Analytics.sendVisitEvent(where = where)
    }

    private companion object {

        const val CREATION_EVENT = "page_creation"
        const val VISIT_TYPE = "visit_type"
    }
}
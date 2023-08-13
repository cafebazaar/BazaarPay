package ir.cafebazaar.bazaarpay.base

import android.content.Context
import androidx.fragment.app.Fragment
import ir.cafebazaar.bazaarpay.analytics.plugins.CloseEventPlugin
import ir.cafebazaar.bazaarpay.analytics.plugins.VisitEventPlugin

/**
 *
 * @property where is the fragment name which sending in `close` and `visit` actionLogs
 */
internal open class BaseFragment(internal val where: String) : Fragment() {

    private val closePlugin by lazy {
        CloseEventPlugin(where, requireActivity()).also {
            lifecycle.addObserver(it)
        }
    }

    private val visitPlugin by lazy {
        VisitEventPlugin(where).also {
            lifecycle.addObserver(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        closePlugin.onAttach(context)
        visitPlugin.onAttach(context)
    }
}
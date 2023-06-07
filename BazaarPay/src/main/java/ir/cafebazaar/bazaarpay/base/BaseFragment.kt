package ir.cafebazaar.bazaarpay.base

import android.content.Context
import androidx.fragment.app.Fragment
import ir.cafebazaar.bazaarpay.analytics.plugins.CloseEventPlugin
import ir.cafebazaar.bazaarpay.analytics.plugins.VisitEventPlugin

/**
 *
 * @property fragmentLabel us the fragment name which sending in `close` and `visit` actionLogs
 */
internal open class BaseFragment(private val fragmentLabel: String) : Fragment() {

    private val closePlugin by lazy {
        CloseEventPlugin(fragmentLabel, requireActivity()).also {
            lifecycle.addObserver(it)
        }
    }

    private val visitPlugin by lazy {
        VisitEventPlugin(fragmentLabel).also {
            lifecycle.addObserver(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        closePlugin.onAttach(context)
        visitPlugin.onAttach(context)
    }
}
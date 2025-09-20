package ir.cafebazaar.bazaarpay.extensions

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visibility(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

private const val CLICK_DELAY = 500L

class SafeViewClickListener(
    private val clickListener: ((view: View) -> Unit)?
) : View.OnClickListener {
    private var isEnabled = true
    override fun onClick(v: View) {
        if (isEnabled) {
            isEnabled = false
            clickListener?.invoke(v)
            v.postDelayed(
                { isEnabled = true },
                CLICK_DELAY
            )
        }
    }
}

fun View.setSafeOnClickListener(clickListener: ((view: View) -> Unit)?) {
    setOnClickListener(SafeViewClickListener(clickListener))
}

fun EditText.moveCursorToEnd() {
    setSelection(text?.length ?: 0)
}

fun TextView.setValueIfNotNullOrEmpty(message: String?) {
    if (message.isNullOrEmpty()) {
        gone()
    } else {
        visible()
        text = message
    }
}

internal fun View.applyWindowInsets(
    @WindowInsetsCompat.Type.InsetsType typeMask: Int
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val innerPadding =
            insets.getInsets(typeMask)
        view.setPadding(
            innerPadding.left,
            innerPadding.top,
            innerPadding.right,
            innerPadding.bottom
        )
        insets
    }
}

internal fun View.applyWindowInsetsWithoutTop(
    @WindowInsetsCompat.Type.InsetsType typeMask: Int
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val innerPadding =
            insets.getInsets(typeMask)
        view.setPadding(
            innerPadding.left,
            0,
            innerPadding.right,
            innerPadding.bottom
        )
        insets
    }
}
package ir.cafebazaar.bazaarpay.extensions

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ir.cafebazaar.bazaarpay.R

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
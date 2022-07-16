package ir.cafebazaar.bazaarpay.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.BidiFormatter
import androidx.core.text.TextDirectionHeuristicsCompat
import ir.cafebazaar.bazaarpay.R

internal class LocalAwareTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {

        val attr = context.obtainStyledAttributes(attrs, R.styleable.LocalAwareTextView, 0, 0)
        val localeGravity = attr.getInt(R.styleable.LocalAwareTextView_gravity, 0)
        attr.recycle()
        gravity = when (localeGravity) {
            1 -> if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                Gravity.LEFT
            } else {
                Gravity.RIGHT
            }
            else -> if (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                Gravity.RIGHT
            } else {
                Gravity.LEFT
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        BidiFormatter.getInstance(textLocale).run {

            val heuristic = if (resources.configuration.layoutDirection == LAYOUT_DIRECTION_RTL) {
                TextDirectionHeuristicsCompat.RTL
            } else {
                TextDirectionHeuristicsCompat.LTR
            }

            super.setText(unicodeWrap(text, heuristic), type)
        }
    }
}
package ir.cafebazaar.bazaarpay.screens.payment.webpage

import android.animation.FloatEvaluator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.setPadding

class UrlBarView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attributeSet, defStyleAttr) {

    private val evaluator = FloatEvaluator()

    private val textView: TextView
    private val surfaceBackground = GradientDrawable().also {
        it.setColor(Color.DKGRAY)
        it.cornerRadius = 8 * resources.displayMetrics.density
    }

    var fraction = 0f
        set(value) {
            if (field != value) {
                field = value
                applyAnimationValue()
            }
        }

    var text: String
        get() = textView.text.toString()
        set(value) {
            textView.text = value
        }

    init {
        val padding = (8 * resources.displayMetrics.density).toInt()
        setPadding(padding)
        setBackgroundColor(Color.BLACK)

        textView = TextView(context).apply {
            setTextColor(Color.WHITE)
            setPadding(padding, 0, padding, 0)
            setSingleLine()
            background = surfaceBackground
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER
        }
        addView(textView, LayoutParams(-1, -1, Gravity.CENTER))
        applyAnimationValue()
    }

    private fun applyAnimationValue() {
        val density = resources.displayMetrics.density
        surfaceBackground.alpha = evaluate(0, 255).toInt()
        textView.textSize = evaluate(12, 18)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.elevation = evaluate(0, 8)
        }
        layoutParams?.height = (evaluate(32, 64) * density).toInt()
        requestLayout()
    }

    private fun evaluate(start: Number, end: Number): Float {
        return evaluator.evaluate(fraction, start, end)
    }
}
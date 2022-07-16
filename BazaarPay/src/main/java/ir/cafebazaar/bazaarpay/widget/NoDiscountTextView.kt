package ir.cafebazaar.bazaarpay.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

internal class NoDiscountTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun strikeThrough() {
        val flag = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        paintFlags = flag
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            strikeThrough()
        }
    }
}
package ir.cafebazaar.bazaarpay.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import ir.cafebazaar.bazaarpay.extensions.isRTL

internal class RTLImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        scaleX = if (context.isRTL()) {
            -1F
        } else {
            1F
        }
    }
}
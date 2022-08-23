package ir.cafebazaar.bazaarpay.widget.errorview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import ir.cafebazaar.bazaarpay.databinding.ViewErrorNotFoundBinding

class NotFoundErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewErrorNotFoundBinding? = null
    private val binding: ViewErrorNotFoundBinding
        get() = requireNotNull(_binding)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        _binding = ViewErrorNotFoundBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setMessageText(message: String) {
        if (message.isNotEmpty()) {
            binding.notFoundText.text = message
        }
    }
}
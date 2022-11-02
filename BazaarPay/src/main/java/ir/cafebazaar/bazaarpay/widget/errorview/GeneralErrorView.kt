package ir.cafebazaar.bazaarpay.widget.errorview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import ir.cafebazaar.bazaarpay.databinding.ViewErrorGeneralBinding
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener

class GeneralErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewErrorGeneralBinding? = null
    private val binding: ViewErrorGeneralBinding
        get() = requireNotNull(_binding)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        _binding = ViewErrorGeneralBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setOnRetryButtonClickListener(callBack: () -> Unit) {
        binding.retryButton.setSafeOnClickListener {
            callBack.invoke()
        }
    }
}
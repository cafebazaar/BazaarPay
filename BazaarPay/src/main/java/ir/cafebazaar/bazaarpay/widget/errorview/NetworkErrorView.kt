package ir.cafebazaar.bazaarpay.widget.errorview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import ir.cafebazaar.bazaarpay.databinding.ViewErrorNetworkBinding
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener

class NetworkErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewErrorNetworkBinding? = null
    private val binding: ViewErrorNetworkBinding
        get() = requireNotNull(_binding)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        _binding = ViewErrorNetworkBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setOnRetryButtonClickListener(callBack: () -> Unit) {
        binding.retryButton.setSafeOnClickListener {
            callBack.invoke()
        }
    }
}
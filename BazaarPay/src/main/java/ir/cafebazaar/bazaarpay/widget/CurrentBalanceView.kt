package ir.cafebazaar.bazaarpay.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import ir.cafebazaar.bazaarpay.databinding.ViewCurrentBalanceBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.getBalanceTextColor

internal class CurrentBalanceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewCurrentBalanceBinding? = null
    private val binding: ViewCurrentBalanceBinding
        get() = requireNotNull(_binding)

    init {
        gravity = Gravity.CENTER_VERTICAL
        _binding = this.bindWithRTLSupport({ layoutInflater, viewGroup, _ ->
            ViewCurrentBalanceBinding.inflate(
                layoutInflater,
                viewGroup
            )
        })
    }

    fun setBalance(
        balance: Long,
        balanceString: String
    ) {
        binding.balanceTextView.apply {
            text = balanceString
            setTextColor(
                getBalanceTextColor(context, balance)
            )
        }
    }
}
package ir.cafebazaar.bazaarpay.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import ir.cafebazaar.bazaarpay.databinding.ViewCurrentBalanceBinding
import ir.cafebazaar.bazaarpay.extensions.getBalanceTextColor

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
        _binding = ViewCurrentBalanceBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setBalance(
        balance: Long,
        balanceString: String
    ) {
        binding.balanceTextView.apply {
            text = balanceString
            setTextColor(
                context.getBalanceTextColor(balance)
            )
        }
    }
}
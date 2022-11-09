package ir.cafebazaar.bazaarpay.widget.errorview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.StringRes
import ir.cafebazaar.bazaarpay.databinding.ViewNotLoginBinding
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener

class NotLoginErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewNotLoginBinding? = null
    private val binding: ViewNotLoginBinding
        get() = requireNotNull(_binding)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        _binding = ViewNotLoginBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setTitleResId(@StringRes resId: Int) {
        binding.forceLoginViewTitle.text = context.getString(resId)
    }

    fun setOnLoginButtonClickListener(callBack: () -> Unit) {
        binding.loginButton.setSafeOnClickListener {
            callBack.invoke()
        }
    }
}
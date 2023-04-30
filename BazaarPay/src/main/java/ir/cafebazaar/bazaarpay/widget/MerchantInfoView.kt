package ir.cafebazaar.bazaarpay.widget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.databinding.ViewMerchantInfoBinding
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarPayImageLoader

internal class MerchantInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewMerchantInfoBinding? = null
    private val binding: ViewMerchantInfoBinding
        get() = requireNotNull(_binding)

    init {
        _binding = this.bindWithRTLSupport({ layoutInflater, viewGroup, _ ->
            ViewMerchantInfoBinding.inflate(
                layoutInflater,
                viewGroup
            )
        })
    }

    fun setMerchantName(
        merchantName: String?
    ) {
        with(binding) {
            productNameTextView.text = merchantName
        }
    }

    fun setMerchantInfo(
        merchantInfo: String?
    ) {
        with(binding) {
            dealerInfoTextView.text = merchantInfo
        }
    }

    fun setMerchantIcon(iconUrl: String?) {
        with(binding.dealerIconImageView) {
            if (!iconUrl.isNullOrEmpty()) {
                BazaarPayImageLoader.loadImage(
                    imageView = this,
                    imageURI = iconUrl,
                    placeHolderId = R.drawable.ic_logo_placeholder
                )
            } else {
                setImageResource(R.drawable.ic_logo_placeholder)
            }
        }
    }

    fun setPrice(price: String?) {
        binding.priceBeforeDiscountView.gone()
        price?.let { binding.paymentPriceView.text = it }
    }
}
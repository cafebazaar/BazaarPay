package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.databinding.ItemPaymentDynamicCreditBinding
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.Option
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener

internal class DynamicCreditOptionViewHolder(
    private val binding: ItemPaymentDynamicCreditBinding,
    onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setSafeOnClickListener {
            onItemClicked(absoluteAdapterPosition)
        }
    }

    fun onBindViewHolder(creditOption: Option) {
        with(binding) {
            subtitle.text = creditOption.label
            subtitle.setBackgroundResource(
                if (creditOption.isSelected) {
                    R.drawable.selector_button_flat_light
                } else {
                    R.drawable.selector_button_flat_secondary
                }
            )
            subtitle.setTextColor(
                ContextCompat.getColor(
                    subtitle.context,
                    if (creditOption.isSelected) {
                        R.color.app_brand_primary
                    } else {
                        R.color.text_secondary
                    }
                )
            )
        }
    }
}
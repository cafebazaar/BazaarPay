package ir.cafebazaar.bazaarpay.screens.payment.paymentmethods

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.databinding.ItemPaymentOptionBinding
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethod
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.utils.imageloader.ImageLoader

internal class PaymentMethodViewHolder(
    private val binding: ItemPaymentOptionBinding,
    private val clickListener: PaymentMethodsClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PaymentMethod, selectedPosition: Int) {
        itemView.setSafeOnClickListener { clickListener.onItemClick(absoluteAdapterPosition) }
        with(binding) {
            item.iconUrl?.let {
                ImageLoader.loadImage(
                    imageView = icon,
                    imageURI = it,
                    placeHolderId = R.drawable.ic_default_payment_old
                )
            }
            title.text = item.title
            description.text = item.description
            optionDescription?.let {
                it.text = item.subDescription
                optionRoot.post {
                    animateDescription(
                        item.subDescription?.isNotEmpty()?.and(
                            selectedPosition == absoluteAdapterPosition
                        ) == true
                    )
                }
            }
            optionRoot.background = getBackgroundDrawable(
                binding.root.context,
                selectedPosition == absoluteAdapterPosition,
                item.subDescription?.isNotEmpty() == true
            )
        }
    }

    private fun animateDescription(isDescriptionVisible: Boolean) {
        binding.optionDescription?.let { optionDescription ->
            optionDescription.clearAnimation()
            ValueAnimator.ofInt(
                optionDescription.measuredHeight,
                getFinalHeight(isDescriptionVisible)
            ).apply {
                addUpdateListener { valueAnimator ->
                    val height = valueAnimator.animatedValue as Int
                    setOptionDescriptionHeight(height)
                }
                duration = ANIMATION_DURATION
                start()
            }
        }
    }

    private fun getFinalHeight(isDescriptionVisible: Boolean): Int {
        return if (isDescriptionVisible) {
            binding.optionRoot.measuredHeight
        } else {
            INVISIBLE_HEIGHT
        }
    }

    private fun setOptionDescriptionHeight(newHeight: Int) {
        binding.optionDescription?.updateLayoutParams {
            height = newHeight
        }
    }

    private fun getBackgroundDrawable(
        context: Context,
        isSelected: Boolean,
        hasSubDescription: Boolean
    ): Drawable? {
        val resId = when {
            isSelected.not() -> R.drawable.border_round_8
            hasSubDescription -> R.drawable.border_round_top_only_green
            else -> R.drawable.border_round_green
        }
        return ContextCompat.getDrawable(context, resId)
    }

    private companion object {
        const val ANIMATION_DURATION = 500L
        const val INVISIBLE_HEIGHT = 0
    }
}
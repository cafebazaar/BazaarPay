package ir.cafebazaar.bazaarpay.screens.payment.directdebitonboarding

import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.databinding.ItemDirectDebitOnboardingBinding
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.OnBoardingItem
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarPayImageLoader

internal class DirectDebitOnBoardingViewHolder(
    private val binding: ItemDirectDebitOnboardingBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(onBoardingItem: OnBoardingItem) {
        with(binding) {
            title.text = onBoardingItem.title
            description.text = onBoardingItem.description
            if (onBoardingItem.getImageUri().isNotEmpty()) {
                BazaarPayImageLoader.loadImage(
                    image,
                    onBoardingItem.getImageUri()
                )
            }
        }
    }
}
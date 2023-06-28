package ir.cafebazaar.bazaarpay.screens.payment.directdebitonboarding

import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.OnBoardingItem
import ir.cafebazaar.bazaarpay.databinding.ItemDirectDebitOnboardingBinding
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarPayImageLoader

internal class DirectDebitOnBoardingViewHolder(
    private val binding: ItemDirectDebitOnboardingBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBindViewHolder(item: OnBoardingItem) {
        with(binding) {
            subtitle.text = item.description
            title.text = item.title
            item.icon?.getImageUriFromThemedIcon(root.context)?.let { image ->
                BazaarPayImageLoader.loadImage(
                    imageView = icon,
                    imageURI = image
                )
            }

        }
    }
}
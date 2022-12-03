package ir.cafebazaar.bazaarpay.screens.payment.directdebitactivating.banklist

import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.databinding.ItemBankListBinding
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarPayImageLoader

internal class DirectDebitBanksListItemViewHolder(
    val binding: ItemBankListBinding,
    val onItemSelected: (BankList.BankListRowItem) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(bank: BankList.BankListRowItem) {
        with(binding) {
            root.setSafeOnClickListener {
                onItemSelected.invoke(bank)
            }
            root.background = bank.getBackgroundResId(root.context)
            bank.model.icon.getImageUriFromThemedIcon().let { image ->
                BazaarPayImageLoader.loadImage(
                    imageView = iconImageView,
                    imageURI = image
                )
            }
            radioButton.isChecked = bank.isSelected
            bankNameTextView.text = bank.model.name
            descriptionTextView.text = bank.model.description
        }
    }
}
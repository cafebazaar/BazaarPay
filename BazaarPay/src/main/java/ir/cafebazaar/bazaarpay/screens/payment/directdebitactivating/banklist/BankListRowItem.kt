package ir.cafebazaar.bazaarpay.screens.payment.directdebitactivating.banklist

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.databinding.ItemBankListBinding
import ir.cafebazaar.bazaarpay.databinding.ItemBankListHeaderBinding
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.Bank

internal abstract sealed class BankList {

    abstract val viewType: Int

    abstract fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    class BankListRowItem(
        val model: Bank,
        val onItemSelected: (BankListRowItem) -> Unit
    ) : BankList() {

        var isSelected: Boolean = false

        fun getBackgroundResId(context: Context): Drawable? {
            val resId = if (isSelected) {
                R.drawable.background_green_10_radius_8
            } else {
                R.drawable.background_grey_10_radius_8
            }
            return ContextCompat.getDrawable(context, resId)
        }

        override val viewType: Int = BankListType.ITEM.ordinal

        override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return DirectDebitBanksListItemViewHolder(
                ItemBankListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemSelected
            )
        }

        fun getImageUri(context: Context): String {
            return model.icon.getImageUriFromThemedIcon()
        }
    }

    object BankListHeaderItem : BankList() {

        override val viewType: Int = BankListType.HEADER.ordinal

        override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return DirectDebitBanksListHeaderViewHolder(
                ItemBankListHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    enum class BankListType {
        ITEM,
        HEADER
    }
}
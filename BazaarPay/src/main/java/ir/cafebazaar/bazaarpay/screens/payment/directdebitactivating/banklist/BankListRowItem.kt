package ir.cafebazaar.bazaarpay.screens.payment.directdebitactivating.banklist

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.Bank
import ir.cafebazaar.bazaarpay.databinding.ItemBankListBinding
import ir.cafebazaar.bazaarpay.databinding.ItemBankListHeaderBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

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
                parent.bindWithRTLSupport(ItemBankListBinding::inflate),
                onItemSelected
            )
        }
    }

    object BankListHeaderItem : BankList() {

        override val viewType: Int = BankListType.HEADER.ordinal

        override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return DirectDebitBanksListHeaderViewHolder(
                parent.bindWithRTLSupport(ItemBankListHeaderBinding::inflate)
            )
        }
    }

    enum class BankListType {
        ITEM,
        HEADER
    }
}
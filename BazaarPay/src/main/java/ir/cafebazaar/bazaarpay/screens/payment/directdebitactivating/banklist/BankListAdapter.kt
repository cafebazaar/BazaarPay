package ir.cafebazaar.bazaarpay.screens.payment.directdebitactivating.banklist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal class BankListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<BankList>()

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return items.first {
            it.viewType == viewType
        }.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? DirectDebitBanksListItemViewHolder)?.let {
            holder.bind(items[position] as BankList.BankListRowItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<BankList>?) {
        items?.let {
            this.items.addAll(items)
            notifyDataSetChanged()
        }
    }
}
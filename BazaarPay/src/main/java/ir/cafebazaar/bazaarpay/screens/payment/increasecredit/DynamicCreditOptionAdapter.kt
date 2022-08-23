package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.databinding.ItemPaymentDynamicCreditBinding
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.Option

internal class DynamicCreditOptionAdapter(
    private val items: List<Option>,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<DynamicCreditOptionViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DynamicCreditOptionViewHolder {

        return DynamicCreditOptionViewHolder(
            ItemPaymentDynamicCreditBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClicked = onItemClicked
        )
    }

    override fun onBindViewHolder(holder: DynamicCreditOptionViewHolder, position: Int) {
        holder.onBindViewHolder(items[position])
    }

    override fun getItemCount(): Int = items.size
}
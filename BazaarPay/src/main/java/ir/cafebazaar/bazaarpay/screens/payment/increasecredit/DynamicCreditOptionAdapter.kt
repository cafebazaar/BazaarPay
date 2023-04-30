package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.Option
import ir.cafebazaar.bazaarpay.databinding.ItemPaymentDynamicCreditBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

internal class DynamicCreditOptionAdapter(
    private val items: List<Option>,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<DynamicCreditOptionViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DynamicCreditOptionViewHolder {

        return DynamicCreditOptionViewHolder(
            parent.bindWithRTLSupport(ItemPaymentDynamicCreditBinding::inflate),
            onItemClicked = onItemClicked
        )
    }

    override fun onBindViewHolder(holder: DynamicCreditOptionViewHolder, position: Int) {
        holder.onBindViewHolder(items[position])
    }

    override fun getItemCount(): Int = items.size
}
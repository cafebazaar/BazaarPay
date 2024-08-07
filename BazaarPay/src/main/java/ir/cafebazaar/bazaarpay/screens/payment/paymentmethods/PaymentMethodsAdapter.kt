package ir.cafebazaar.bazaarpay.screens.payment.paymentmethods

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethod
import ir.cafebazaar.bazaarpay.databinding.ItemPaymentOptionBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

internal class PaymentMethodsAdapter(private val paymentOptionClickListener: PaymentMethodsClickListener) :
    RecyclerView.Adapter<PaymentMethodViewHolder>() {

    val items = mutableListOf<PaymentMethod>()

    var selectedPosition = DEFAULT_SELECTED_OPTION
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        return PaymentMethodViewHolder(
            parent.bindWithRTLSupport(ItemPaymentOptionBinding::inflate),
            clickListener = paymentOptionClickListener
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bind(items[position], selectedPosition)
    }

    fun setSelectedPosition(newPosition: Int) {
        if (newPosition == selectedPosition) {
            return
        }

        if (newPosition < items.size) {
            val oldPosition = selectedPosition
            selectedPosition = newPosition
            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)
        } else {
            Throwable("Illegal item selection in invoice bottom sheet")
        }
    }

    fun getSelectedOptionTitle(): String {
        return items.getOrNull(selectedPosition)?.title.orEmpty()
    }

    companion object {

        const val DEFAULT_SELECTED_OPTION = 0
    }
}
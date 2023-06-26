package ir.cafebazaar.bazaarpay.screens.payment.directdebitonboarding

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.OnBoardingItem
import ir.cafebazaar.bazaarpay.databinding.ItemDirectDebitOnboardingBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

internal class DirectDebitOnBoardingAdapter :
    RecyclerView.Adapter<DirectDebitOnBoardingViewHolder>() {

    private var items = mutableListOf<OnBoardingItem>()

    fun setItems(items: List<OnBoardingItem>?) {
        items ?: return
        this.items.clear()
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DirectDebitOnBoardingViewHolder {

        return DirectDebitOnBoardingViewHolder(
            parent.bindWithRTLSupport(ItemDirectDebitOnboardingBinding::inflate)
        )
    }

    override fun onBindViewHolder(holder: DirectDebitOnBoardingViewHolder, position: Int) {
        holder.onBindViewHolder(items[position])
    }

    override fun getItemCount(): Int = items.size
}
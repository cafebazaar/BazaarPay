package ir.cafebazaar.bazaarpay.screens.payment.directdebitonboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.databinding.ItemDirectDebitOnboardingBinding
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.OnBoardingItem

internal class DirectDebitOnBoardingAdapter(
    private val onBoardingItemModels: List<OnBoardingItem>
) : RecyclerView.Adapter<DirectDebitOnBoardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectDebitOnBoardingViewHolder {
        return DirectDebitOnBoardingViewHolder(
            ItemDirectDebitOnboardingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DirectDebitOnBoardingViewHolder, position: Int) {
        holder.bind(onBoardingItemModels[position])
    }

    override fun getItemCount(): Int {
        return onBoardingItemModels.size
    }
}
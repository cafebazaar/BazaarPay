package ir.cafebazaar.bazaarpay

import android.os.Bundle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.base.BaseFragment
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.extensions.navigateSafe

internal class StartPaymentFragment : BaseFragment(PAGE_NAME) {

    private val accountRepository: AccountRepository by lazy {
        ServiceLocator.get()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            findNavController().navigateSafe(getNavDirection())
        }
    }

    private fun getNavDirection(): NavDirections {
        return when (accountRepository.needLogin().not()) {
            true -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToPaymentMethodsFragment()
            }

            false -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToRegisterFragment()
            }
        }
    }

    private companion object {

        const val PAGE_NAME = "StartPayment"
    }
}
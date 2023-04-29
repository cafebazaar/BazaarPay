package ir.cafebazaar.bazaarpay

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ir.cafebazaar.bazaarpay.arg.PaymentTypeArg
import ir.cafebazaar.bazaarpay.extensions.navigateSafe
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository

internal class StartPaymentFragment: Fragment() {

    private val accountRepository: AccountRepository by lazy {
        ServiceLocator.get()
    }

    private val args: StartPaymentFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            findNavController().navigateSafe(getNavDirection())
        }
    }

    private fun getNavDirection(): NavDirections {
        return when (accountRepository.isLoggedIn()) {
            true -> {
                when(args.paymentType) {
                    PaymentTypeArg.PURCHASE -> {
                        StartPaymentFragmentDirections.actionStartPaymentFragmentToPaymentMethodsFragment()
                    }
                    PaymentTypeArg.INCREASE_BALANCE -> {
                        StartPaymentFragmentDirections.actionStartPaymentFragmentToPaymentDynamicCreditFragment()
                    }
                }
            }
            false -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToRegisterFragment()
            }
        }
    }
}
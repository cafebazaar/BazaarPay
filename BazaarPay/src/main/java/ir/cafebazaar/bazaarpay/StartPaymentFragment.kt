package ir.cafebazaar.bazaarpay

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.extensions.navigateSafe

internal class StartPaymentFragment : Fragment() {

    private val accountRepository: AccountRepository by lazy {
        ServiceLocator.get()
    }

    private val args: BazaarPayActivityArgs? by lazy {
        requireActivity().intent.getParcelableExtra(
            BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS
        )
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
                getNavDirectionBasedOnArguments()
            }

            false -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToRegisterFragment()
            }
        }
    }

    private fun getNavDirectionBasedOnArguments(): NavDirections {
        return when (val bazaarPayArgs = args) {
            is BazaarPayActivityArgs.Normal -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToPaymentMethodsFragment()
            }

            is BazaarPayActivityArgs.DirectPayContract -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToDirectPayContractFragment(
                    contractToken = bazaarPayArgs.contractToken
                )
            }

            else -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToPaymentMethodsFragment()
            }
        }
    }
}
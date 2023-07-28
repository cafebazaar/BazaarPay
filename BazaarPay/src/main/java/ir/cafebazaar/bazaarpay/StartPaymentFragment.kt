package ir.cafebazaar.bazaarpay

import android.content.Context
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

    private var finishCallbacks: FinishCallbacks? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            checkSDKInitType(onLoginType = { return@onActivityCreated })
            findNavController().navigateSafe(getNavDirection())
        }
    }

    override fun onAttach(context: Context) {
        finishCallbacks = context as? FinishCallbacks
            ?: throw IllegalStateException(
                "this activity must implement FinishPaymentCallbacks"
            )
        super.onAttach(context)
    }

    private inline fun checkSDKInitType(onLoginType: () -> Unit) {
        if (accountRepository.needLogin().not() && args is BazaarPayActivityArgs.Login) {
            finishCallbacks?.onSuccess()
            onLoginType.invoke()
        }
    }

    private fun getNavDirection(): NavDirections {
        return if (accountRepository.needLogin().not()) {
            getNavDirectionBasedOnArguments()
        } else {
            StartPaymentFragmentDirections.actionStartPaymentFragmentToRegisterFragment()
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

            is BazaarPayActivityArgs.IncreaseBalance -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToPaymentDynamicCreditFragment()
            }

            else -> {
                StartPaymentFragmentDirections.actionStartPaymentFragmentToPaymentMethodsFragment()
            }
        }
    }

    override fun onDetach() {
        finishCallbacks = null
        super.onDetach()
    }
}
package ir.cafebazaar.bazaarpay.screens.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.StartPaymentFragmentDirections
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.databinding.FragmentLogoutBinding

internal class LogoutFragment : Fragment() {

    private val logoutViewModel: LogoutViewModel by viewModels()

    private val accountRepository: AccountRepository by lazy {
        ServiceLocator.get()
    }

    private var _binding: FragmentLogoutBinding? = null
    private val binding: FragmentLogoutBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogoutBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountRepository.logout()
        findNavController().navigate(StartPaymentFragmentDirections.openSignin())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
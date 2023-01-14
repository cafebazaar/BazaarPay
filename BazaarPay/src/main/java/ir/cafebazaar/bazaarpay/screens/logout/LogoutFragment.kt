package ir.cafebazaar.bazaarpay.screens.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.databinding.FragmentLogoutBinding
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener

internal class LogoutFragment : Fragment() {

    private val logoutViewModel: LogoutViewModel by viewModels()

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
        logoutViewModel.navigationLiveData.observe(viewLifecycleOwner, ::onNavigationReceived)
        binding.logoutButton.setSafeOnClickListener {
            logoutViewModel.onLogoutClicked()
        }
        binding.cancelButton.setSafeOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onNavigationReceived(navDirection: NavDirections) {
        findNavController().navigate(navDirection)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
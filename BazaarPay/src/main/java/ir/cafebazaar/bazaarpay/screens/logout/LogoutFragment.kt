package ir.cafebazaar.bazaarpay.screens.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.analytics.Analytics.WHAT_KEY
import ir.cafebazaar.bazaarpay.base.BaseFragment
import ir.cafebazaar.bazaarpay.databinding.FragmentLogoutBinding
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

internal class LogoutFragment : BaseFragment(SCREEN_NAME) {

    private val logoutViewModel: LogoutViewModel by viewModels()

    private var _binding: FragmentLogoutBinding? = null
    private val binding: FragmentLogoutBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflater.bindWithRTLSupport(FragmentLogoutBinding::inflate, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutViewModel.navigationLiveData.observe(viewLifecycleOwner, ::onNavigationReceived)
        binding.logoutButton.setSafeOnClickListener {
            sendLogoutEvent()
            logoutViewModel.onLogoutClicked()
        }
        binding.cancelButton.setSafeOnClickListener {
            sendDeclineLogoutEvent()
            findNavController().popBackStack()
        }
    }

    private fun sendLogoutEvent() {
        Analytics.sendClickEvent(where = SCREEN_NAME, what = hashMapOf(WHAT_KEY to CLICK_LOG_OUT))
    }

    private fun sendDeclineLogoutEvent() {
        Analytics.sendClickEvent(
            where = SCREEN_NAME,
            what = hashMapOf(WHAT_KEY to CLICK_DECLINE_LOG_OUT)
        )
    }

    private fun onNavigationReceived(navDirection: NavDirections) {
        findNavController().navigate(navDirection)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        internal const val SCREEN_NAME = "logout"
        internal const val CLICK_LOG_OUT = "clickLogOut"
        private const val CLICK_DECLINE_LOG_OUT = "click_decline_log_out"
    }
}
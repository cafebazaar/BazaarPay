package ir.cafebazaar.bazaarpay.screens.directPay

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import ir.cafebazaar.bazaarpay.FinishCallbacks
import ir.cafebazaar.bazaarpay.databinding.FragmentDirectPayContractBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

class DirectPayContractFragment : Fragment() {

    private var _binding: FragmentDirectPayContractBinding? = null
    private val binding: FragmentDirectPayContractBinding
        get() = requireNotNull(_binding)

    private var finishCallbacks: FinishCallbacks? = null

    override fun onAttach(context: Context) {
        finishCallbacks = context as? FinishCallbacks
            ?: throw IllegalStateException(
                "this activity must implement FinishPaymentCallbacks"
            )
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflater.bindWithRTLSupport(FragmentDirectPayContractBinding::inflate, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    private fun handleBackPress() {
        finishCallbacks?.onCanceled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        finishCallbacks = null
        super.onDetach()
    }
}
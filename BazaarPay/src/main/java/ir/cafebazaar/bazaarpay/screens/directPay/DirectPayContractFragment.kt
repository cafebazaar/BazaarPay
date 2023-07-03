package ir.cafebazaar.bazaarpay.screens.directPay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.cafebazaar.bazaarpay.databinding.FragmentDirectPayContractBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

class DirectPayContractFragment : Fragment() {

    private var _binding: FragmentDirectPayContractBinding? = null
    private val binding: FragmentDirectPayContractBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflater.bindWithRTLSupport(FragmentDirectPayContractBinding::inflate, container)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
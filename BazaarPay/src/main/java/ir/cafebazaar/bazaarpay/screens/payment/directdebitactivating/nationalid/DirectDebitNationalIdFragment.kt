package ir.cafebazaar.bazaarpay.screens.payment.directdebitactivating.nationalid

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.databinding.FragmentNationalIdBinding
import ir.cafebazaar.bazaarpay.extensions.NATIONAL_ID_LENGTH
import ir.cafebazaar.bazaarpay.extensions.hideKeyboard
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener

internal class DirectDebitNationalIdFragment : Fragment() {

    private var _binding: FragmentNationalIdBinding? = null
    private val binding: FragmentNationalIdBinding
        get() = requireNotNull(_binding)

    private val directDebitNationalIdViewModel: DirectDebitNationalIdViewModel by viewModels()

    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNationalIdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(directDebitNationalIdViewModel) {
            navigationLiveData.observe(viewLifecycleOwner, ::handleNavigation)
            showInvalidErrorLiveData.observe(viewLifecycleOwner) {
                showError()
            }
        }
        initUI(view)
    }

    private fun initUI(view: View) {
        with(binding) {
            acceptButton.isEnabled = false
            textWatcher = nationalIdEditText.doAfterTextChanged {
                hideError()
                acceptButton.isEnabled = it?.length == NATIONAL_ID_LENGTH
            }
            acceptButton.setSafeOnClickListener {
                directDebitNationalIdViewModel.onAcceptClicked(
                    nationalIdEditText.text.toString()
                )
            }
            toolbarBack.setSafeOnClickListener {
                hideKeyboard(nationalIdEditText.windowToken)
                findNavController().popBackStack()
            }
        }
    }

    private fun showError() {
        binding.nationalIdInput.apply {
            isErrorEnabled = true
            error = context.getString(R.string.bazaarpay_invalid_national_id_error)
        }
    }

    private fun hideError() {
        binding.nationalIdInput.isErrorEnabled = false
    }

    private fun handleNavigation(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }

    override fun onDestroyView() {
        textWatcher?.let { binding.nationalIdEditText.removeTextChangedListener(it) }
        textWatcher = null
        super.onDestroyView()
        _binding = null
    }
}
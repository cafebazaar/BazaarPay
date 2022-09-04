package ir.cafebazaar.bazaarpay.screens.login.register

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.FinishCallbacks
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.databinding.FragmentRegisterBinding
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.models.InvalidPhoneNumberException
import ir.cafebazaar.bazaarpay.extensions.fromHtml
import ir.cafebazaar.bazaarpay.extensions.getReadableErrorMessage
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.hideKeyboard
import ir.cafebazaar.bazaarpay.extensions.isLandscape
import ir.cafebazaar.bazaarpay.extensions.isValidPhoneNumber
import ir.cafebazaar.bazaarpay.extensions.navigateSafe
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener

internal class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
        get() = requireNotNull(_binding)

    private var phoneEditTextWatcher: TextWatcher? = null

    private val viewModel: RegisterViewModel by viewModels()
    private val phoneNumber: String
        get() = binding.phoneEditText.text.toString()
    private var finishCallBacks: FinishCallbacks? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        finishCallBacks = context as? FinishCallbacks
            ?: throw IllegalStateException("this activity must implement FinishLoginCallBack")
    }

    override fun onDetach() {
        super.onDetach()
        finishCallBacks = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            data.observe(viewLifecycleOwner, ::handleResourceState)
            savedPhones.observe(viewLifecycleOwner, ::populateAutoFillPhoneNumbers)
            loadSavedPhones()
        }

        initUi()
    }

    private fun initUi() {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            finishCallBacks?.onCanceled()
        }

        binding.close.setSafeOnClickListener {
            finishCallBacks?.onCanceled()
        }

        disableProceedButtonWhenEditTextIsEmpty()

        binding.proceedBtn.setSafeOnClickListener { register() }

        setLoginInfo()
    }

    private fun setLoginInfo() {
        with(binding) {
            messageTextView.gone()
        }
    }

    override fun onResume() {
        super.onResume()
        setPrivacyAndTerms()
    }

    override fun onDestroyView() {
        binding.phoneEditText.setAdapter(null)
        binding.phoneEditText.removeTextChangedListener(phoneEditTextWatcher)
        super.onDestroyView()
        _binding = null
    }

    private fun setPrivacyAndTerms() {
        binding.privacyAndTerms.run {
            text = getString(R.string.privacy_and_terms_login).fromHtml()
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun disableProceedButtonWhenEditTextIsEmpty() {
        binding.phoneEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> register()
                else -> false
            }
        }

        binding.proceedBtn.isEnabled = binding.phoneEditText.text.toString().isValidPhoneNumber()
        phoneEditTextWatcher = binding.phoneEditText.doOnTextChanged { text, _, _, _ ->
            binding.proceedBtn.isEnabled = text.toString().isValidPhoneNumber()
            hideError()
        }
    }

    private fun register(): Boolean {
        return if (phoneNumber.isValidPhoneNumber()) {
            viewModel.register(phoneNumber)
            true
        } else {
            false
        }
    }

    private fun populateAutoFillPhoneNumbers(phonesList: List<String>) {
        binding.phoneEditText.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    phonesList.toTypedArray()
                )
            )
            threshold = 1
        }
    }

    private fun handleResourceState(
        resource: Resource<WaitingTimeWithEnableCall>?
    ) {
        resource?.let {
            when (it.resourceState) {
                ResourceState.Success -> {
                    resource.data?.let {
                        handleSuccess(resource.data)
                    }
                }
                ResourceState.Error -> {
                    val message = requireContext().getReadableErrorMessage(resource.failure)
                    showError(message)
                }
                ResourceState.Loading -> handleLoading()
                else -> Throwable("Illegal State in handleResourceState")
            }
        }
    }

    private fun handleSuccess(
        waitingTimeWithEnableCall: WaitingTimeWithEnableCall
    ) {
        if (requireContext().isLandscape) {
            hideKeyboard(binding.phoneEditText.windowToken)
        }

        binding.proceedBtn.isLoading = false
        hideError()
        findNavController().navigateSafe(
            RegisterFragmentDirections.actionRegisterFragmentToVerifyOtpFragment(
                phoneNumber,
                waitingTimeWithEnableCall
            )
        )
    }

    private fun hideError() {
        binding.phoneInputLayout.isErrorEnabled = false
    }

    private fun showError(message: String) {
        with(binding) {
            proceedBtn.isLoading = false
            phoneInputLayout.isErrorEnabled = true
            phoneInputLayout.error = message
        }
        hideKeyboardInLandscape()
    }

    private fun handleLoading() {
        hideError()
        binding.proceedBtn.isLoading = true
        if (requireContext().isLandscape) {
            hideKeyboard(binding.phoneEditText.windowToken)
        }
    }

    private fun hideKeyboardInLandscape() {
        if (requireContext().isLandscape) {
            hideKeyboard(binding.phoneEditText.windowToken)
        }
    }
}
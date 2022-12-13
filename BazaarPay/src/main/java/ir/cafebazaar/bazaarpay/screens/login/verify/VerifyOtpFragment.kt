package ir.cafebazaar.bazaarpay.screens.login.verify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import ir.cafebazaar.bazaarpay.FinishCallbacks
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.databinding.FragmentVerifyOtpBinding
import ir.cafebazaar.bazaarpay.databinding.LayoutVerifyHeaderBinding
import ir.cafebazaar.bazaarpay.extensions.getReadableErrorMessage
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.hideKeyboard
import ir.cafebazaar.bazaarpay.extensions.invisible
import ir.cafebazaar.bazaarpay.extensions.isGooglePlayServicesAvailable
import ir.cafebazaar.bazaarpay.extensions.isLandscape
import ir.cafebazaar.bazaarpay.extensions.localizeNumber
import ir.cafebazaar.bazaarpay.extensions.observe
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.models.VerificationState
import ir.cafebazaar.bazaarpay.receiver.SmsPermissionReceiver
import ir.cafebazaar.bazaarpay.screens.login.LoginConstants.ACTION_BROADCAST_LOGIN
import ir.cafebazaar.bazaarpay.screens.login.LoginConstants.SMS_NUMBER
import ir.cafebazaar.bazaarpay.utils.secondsToStringTime

internal class VerifyOtpFragment : Fragment() {

    private val viewModel: VerifyOtpViewModel by viewModels()
    private var finishCallbacks: FinishCallbacks? = null

    private val fragmentArgs: VerifyOtpFragmentArgs by lazy(LazyThreadSafetyMode.NONE) {
        VerifyOtpFragmentArgs.fromBundle(requireArguments())
    }

    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding: FragmentVerifyOtpBinding
        get() = requireNotNull(_binding)

    private var _headerBinding: LayoutVerifyHeaderBinding? = null
    private val headerBinding: LayoutVerifyHeaderBinding
        get() = requireNotNull(_headerBinding)

    private val phoneNumber: String
        get() = fragmentArgs.phoneNumber

    private var verificationCodeWatcher: TextWatcher? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        finishCallbacks = context as? FinishCallbacks
            ?: throw IllegalStateException("this activity must implement FinishLoginCallBack")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(fragmentArgs.waitingTimeWithEnableCall, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerifyOtpBinding.inflate(
            inflater,
            container,
            false
        )

        _headerBinding = LayoutVerifyHeaderBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        headerBinding.close.setSafeOnClickListener {
            findNavController().popBackStack()
        }

        headerBinding.verificationMessageTextView.text = getString(
            R.string.bazaarpay_waiting_for_verification_sms,
            phoneNumber.localizeNumber(requireContext())
        )

        binding.resendCodeButton.setSafeOnClickListener { handleResendSmsClick() }
        binding.callButton.setSafeOnClickListener {
            viewModel.onCallButtonClicked(phoneNumber)
        }
        headerBinding.proceedBtn.setSafeOnClickListener { handleProceedClick(false) }
        // disable when initialized, because there is no text in the input.
        headerBinding.proceedBtn.isEnabled = false
        headerBinding.verificationCodeEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (headerBinding.proceedBtn.isEnabled) {
                        handleProceedClick(false)
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }

        verificationCodeWatcher = headerBinding.verificationCodeEditText.doAfterTextChanged {
            hideError()
            headerBinding.proceedBtn.isEnabled = !it.isNullOrEmpty() &&
                    viewModel.verifyCodeStateLiveData.value?.resourceState != ResourceState.Loading
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(viewModel) {
            observe(verifyCodeStateLiveData, ::handleVerifyCodeState)
            resendSmsAndCallLiveData.observe(viewLifecycleOwner, ::handleResendSmsAndCallState)

            showCallButtonLiveData.observe(viewLifecycleOwner) { showCall ->
                if (showCall) {
                    binding.callButton.visible()
                } else {
                    binding.callButton.gone()
                }
            }

            onStartSmsPermissionLiveData.observe(viewLifecycleOwner, ::onSmsPermission)
            verificationCodeLiveData.observe(viewLifecycleOwner, ::onSmsReceived)
            onActivityCreated()
            startListeningSms()
        }
    }

    private fun startListeningSms() {
        if (requireActivity().isGooglePlayServicesAvailable()) {
            SmsRetriever
                .getClient(requireActivity())
                .startSmsUserConsent(SMS_NUMBER)
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION).also { intentFilter ->
                requireActivity().registerReceiver(SmsPermissionReceiver(), intentFilter)
            }
        }
    }

    private fun onSmsPermission(intent: Intent) {
        startActivityForResult(intent, SMS_CONSENT_REQUEST)
    }

    private fun onSmsReceived(otpCode: String) {
        headerBinding.verificationCodeEditText.setText(otpCode)
        handleProceedClick(true)
    }

    private fun handleVerifyCodeState(resource: Resource<Nothing>?) {
        resource?.let {
            when (it.resourceState) {
                ResourceState.Success -> handleVerifyCodeSuccess()
                ResourceState.Error -> handleVerifyCodeError(
                    requireContext().getReadableErrorMessage(
                        resource.failure
                    )
                )
                ResourceState.Loading -> handleVerifyCodeLoading()
                else -> Throwable("illegal state in handleVerifyCodeState")
            }
        }
    }

    private fun handleVerifyCodeLoading() {
        headerBinding.proceedBtn.isLoading = true
    }

    private fun handleResendSmsClick() {
        viewModel.onResendSmsClicked(phoneNumber)
    }

    private fun handleProceedClick(isAutomatic: Boolean) {
        val code = headerBinding.verificationCodeEditText.text?.toString() ?: ""
        viewModel.verifyCode(phoneNumber, code)
    }

    private fun handleVerifyCodeSuccess() {
        headerBinding.proceedBtn.isLoading = false

        sendLoginBroadcast()
        hideKeyboardInLandscape()

        findNavController().navigate(
            VerifyOtpFragmentDirections.actionVerifyOtpFragmentToPaymentMethodsFragment()
        )
    }

    private fun sendLoginBroadcast() {
        Intent().apply {
            setPackage(requireContext().packageName)
            action = ACTION_BROADCAST_LOGIN
        }.also {
            requireContext().sendBroadcast(it)
        }
    }

    private fun handleVerifyCodeError(message: String) {
        headerBinding.proceedBtn.isLoading = false
        showError(message)
        hideKeyboardInLandscape()
    }

    private fun showError(message: String) {

        headerBinding.verificationCodeInput.isErrorEnabled = true
        headerBinding.verificationCodeInput.error = message
    }

    private fun hideError() {
        headerBinding.verificationCodeInput.isErrorEnabled = false
    }

    private fun handleResendSmsAndCallState(resource: Resource<Long>) {

        when (resource.resourceState) {
            ResourceState.Success -> {
                resource.data?.let(::onCountDownStarted)
            }
            ResourceState.Error -> {
                resource.data?.let(::onCountDownStarted)
                showError(requireContext().getReadableErrorMessage(resource.failure))
            }
            ResourceState.Loading -> {
                binding.resendCodeButton.invisible()
                binding.callButton.invisible()
                binding.resendText.invisible()
                hideKeyboardInLandscape()
            }
            VerificationState.Tick -> {
                resource.data?.let(::onTick)
            }
            VerificationState.FinishCountDown -> {
                onCountDownFinished()
            }
            else -> Throwable("illegal state in handleResendSmsAndCallState")
        }
    }

    private fun onCountDownStarted(waitingTime: Long) {
        binding.resendCodeButton.invisible()
        binding.callButton.invisible()
        binding.resendText.visible()
    }

    private fun onTick(waitingTime: Long) {
        val untilFinishTimeText = secondsToStringTime(waitingTime)
        binding.resendText.text = getString(R.string.bazaarpay_resend_after, untilFinishTimeText)
    }

    private fun onCountDownFinished() {

        if (!isAdded) return

        binding.resendText.invisible()
        binding.resendCodeButton.visible()
    }

    private fun hideKeyboardInLandscape() {
        if (requireContext().isLandscape) {
            hideKeyboard(headerBinding.verificationCodeEditText.windowToken)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveInstanceState(outState)
    }

    override fun onDetach() {
        super.onDetach()
        finishCallbacks = null
    }

    override fun onDestroyView() {
        headerBinding.verificationCodeEditText.removeTextChangedListener(
            verificationCodeWatcher
        )
        super.onDestroyView()
        _binding = null
        _headerBinding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SMS_CONSENT_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    message?.let {
                        viewModel.onSmsMessage(it)
                    }
                }
        }
    }

    private companion object {

        const val SMS_CONSENT_REQUEST = 2
        const val OTP_TOKEN_START_POSITION = 10
        const val OTP_TOKEN_END_POSITION = 14
    }
}
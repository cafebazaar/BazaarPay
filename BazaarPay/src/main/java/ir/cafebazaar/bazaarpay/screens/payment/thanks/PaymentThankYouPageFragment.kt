package ir.cafebazaar.bazaarpay.screens.payment.thanks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.FinishCallbacks
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.databinding.FragmentThankYouPageBinding
import ir.cafebazaar.bazaarpay.extensions.getReadableErrorMessage
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.persianDigitsIfPersian
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import java.util.*

internal class PaymentThankYouPageFragment : Fragment() {

    private var finishCallbacks: FinishCallbacks? = null

    private val args: PaymentThankYouPageFragmentArgs by lazy(LazyThreadSafetyMode.NONE) {
        PaymentThankYouPageFragmentArgs(isSuccess = true)
    }

    private val paymentThankYouPageViewModel: PaymentThankYouPageViewModel by viewModels()

    private var _binding: FragmentThankYouPageBinding? = null
    private val binding: FragmentThankYouPageBinding
        get() = requireNotNull(_binding)

    override fun onAttach(context: Context) {
        finishCallbacks = context as? FinishCallbacks
            ?: throw IllegalStateException("this activity must implement FinishPaymentCallbacks")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThankYouPageBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            false
        }
        with(paymentThankYouPageViewModel) {
            onDataReceived(args)
            viewStateLiveData.observe(viewLifecycleOwner) {
                handleViewStateResource(it)
            }

            performSuccessLiveData.observe(viewLifecycleOwner) {
                binding.successButton.performClick()
            }
        }
    }

    private fun handleViewStateResource(resource: Resource<PaymentThankYouPageSuccessModel>) {
        when (resource.resourceState) {
            ResourceState.Success -> {
                val paymentThankYouSuccessModel = requireNotNull(resource.data)
                showSuccess(paymentThankYouSuccessModel)
            }
            ResourceState.Error -> showError(resource.failure)
        }
    }

    private fun showSuccess(model: PaymentThankYouPageSuccessModel) {
        with(binding) {
            contentContainer.visible()
            successButton.visible()
            errorBox.gone()

            statusIconImageView.setImageResource(R.drawable.ic_success)

            messageTextView.text =
                model.messageTextModel.argMessage ?: getString(
                    model.messageTextModel.defaultMessageId
                )
            waitingProgressBar.progress = model.paymentProgressBarModel.successMessageCountDown.toInt()
            secondsTextView.text = context?.resources?.getQuantityString(
                R.plurals.bazaarpay_seconds,
                model.paymentProgressBarModel.seconds,
                model.paymentProgressBarModel.seconds
            )?.persianDigitsIfPersian(Locale.getDefault())
            successButton.setSafeOnClickListener {
                finishCallbacks?.onSuccess()
            }
        }
    }

    private fun showError(error: ErrorModel?) {
        with(binding) {
            contentContainer.visible()
            successButton.gone()
            errorBox.visible()

            successButton.setSafeOnClickListener {
                finishCallbacks?.onCanceled()
            }
            successButton.text = getString(R.string.bazaarpay_return_to_payment)
            messageTextView.text = getString(R.string.bazaarpay_payment_failed)
            statusIconImageView.setImageResource(
                error?.getErrorIcon(requireContext()) ?: R.drawable.ic_fail
            )
            errorTextView.text = requireContext().getReadableErrorMessage(error)
        }
    }

    override fun onDetach() {
        finishCallbacks = null
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
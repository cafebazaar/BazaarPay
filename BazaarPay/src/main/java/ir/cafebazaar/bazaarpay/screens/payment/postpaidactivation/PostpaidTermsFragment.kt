package ir.cafebazaar.bazaarpay.screens.payment.postpaidactivation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.databinding.FragmentPostpaidTermsBinding
import ir.cafebazaar.bazaarpay.extensions.getReadableErrorMessage
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.toastMessage
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

internal class PostpaidTermsFragment : Fragment() {

    private val postpaidTermsViewModel: PostpaidTermsViewModel by viewModels()

    private var _binding: FragmentPostpaidTermsBinding? = null
    private val binding: FragmentPostpaidTermsBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflater.bindWithRTLSupport(FragmentPostpaidTermsBinding::inflate, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            backButton.setSafeOnClickListener {
                onBackPressedClicked()
            }

            acceptButton.setSafeOnClickListener {
                postpaidTermsViewModel.acceptButtonClicked()
            }
        }

        observeCreditConfirmationViewModelLiveData()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressedClicked()
        }
    }

    private fun observeCreditConfirmationViewModelLiveData() {
        postpaidTermsViewModel.activationLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                acceptButton.isLoading = it.isLoading
                if (it.isError) {
                    toastMessage(requireContext().getReadableErrorMessage(it.failure))
                }
                if (it.isSuccess) {
                    onBackPressedClicked()
                }
            }
        }
    }

    private fun onBackPressedClicked() {
        findNavController().popBackStack()
    }
}
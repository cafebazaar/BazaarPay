package ir.cafebazaar.bazaarpay.screens.payment.directdebitonboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.databinding.FragmentDirectDebitOnBoardingBinding
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.navigateSafe
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.PaymentFlowState
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.getErrorViewBasedOnErrorModel

internal class DirectDebitOnBoardingFragment : Fragment() {

    private var _binding: FragmentDirectDebitOnBoardingBinding? = null
    private val binding: FragmentDirectDebitOnBoardingBinding
        get() = requireNotNull(_binding)

    private val adapter by lazy { DirectDebitOnBoardingAdapter() }

    private val onBoardingViewModel: DirectDebitOnBoardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflater.bindWithRTLSupport(
            FragmentDirectDebitOnBoardingBinding::inflate,
            container
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    private fun initUI(view: View) {
        observeOnBoardingViewModel()

        with(binding) {
            backButton.setSafeOnClickListener {
                findNavController().popBackStack()
            }
            nextButton.setSafeOnClickListener {
                onBoardingViewModel.onNextButtonClicked()
            }

            directDebitOnBoardingList.adapter = adapter
        }
    }

    private fun observeOnBoardingViewModel() {
        with(onBoardingViewModel) {
            loadData()
            navigationLiveData.observe(viewLifecycleOwner, ::handleNavigation)
            directDebitOnBoardingStates.observe(
                viewLifecycleOwner,
                ::handleDirectDebitOnBoardingStates
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleDirectDebitOnBoardingStates(resource: Resource<DirectDebitOnBoardingDetails>?) {
        resource?.let {
            when (it.resourceState) {
                ResourceState.Loading -> {
                    hideErrorView()
                    with(binding) {
                        contentGroup.gone()
                        loading.visible()
                    }
                }

                PaymentFlowState.DirectDebitObBoardingDetails -> {
                    hideErrorView()
                    with(binding) {
                        contentGroup.visible()
                        loading.gone()
                    }
                    adapter.setItems(it.data?.onBoardingDetails)
                    adapter.notifyDataSetChanged()
                }

                ResourceState.Error -> {
                    if (it.failure is ErrorModel.NetworkConnection) {
                        showErrorView(it.failure)
                    }
                    with(binding) {
                        contentGroup.gone()
                        loading.gone()
                    }
                }
            }
        }
    }

    private fun showErrorView(errorModel: ErrorModel) {
        binding.errorView.apply {
            removeAllViews()
            addView(
                getErrorViewBasedOnErrorModel(
                    requireContext(),
                    errorModel,
                    ::onRetryClicked,
                    ::onLoginClicked
                )
            )
            visible()
        }
    }

    private fun onRetryClicked() {
        onBoardingViewModel.loadData()
    }

    private fun onLoginClicked() {
        findNavController().navigateSafe(
            R.id.open_signin
        )
    }

    private fun hideErrorView() {
        binding.errorView.gone()
    }

    private fun handleNavigation(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
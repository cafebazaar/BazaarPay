package ir.cafebazaar.bazaarpay.screens.payment.directdebitonboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.databinding.FragmentDirectDebitOnBoardingBinding
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.PaymentFlowState
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.OnBoardingItem
import ir.cafebazaar.bazaarpay.utils.getErrorViewBasedOnErrorModel

internal class DirectDebitOnBoardingFragment : Fragment() {

    private var _binding: FragmentDirectDebitOnBoardingBinding? = null
    private val binding: FragmentDirectDebitOnBoardingBinding
        get() = requireNotNull(_binding)

    private var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null
    private var oneShotPreDrawListener: OneShotPreDrawListener? = null

    private val onBoardingViewModel: DirectDebitOnBoardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectDebitOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    private fun initUI(view: View) {
        observeOnBoardingViewModel()

        with(binding) {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            skipButton.setOnClickListener {
                onBoardingViewModel.onSkipButtonClicked()
            }
            nextButton.setOnClickListener { onNextButtonClicked() }
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
                    setUpViewPager((resource.data as DirectDebitOnBoardingDetails).onBoardingDetails)
                    hideErrorView()
                    with(binding) {
                        contentGroup.visible()
                        loading.gone()
                    }
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

    private fun setUpViewPager(items: List<OnBoardingItem>) {
        with(binding) {
            onBoardingViewPager.adapter = DirectDebitOnBoardingAdapter(items)
            wormsDotIndicator.setViewPager2(onBoardingViewPager)
            onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == onBoardingViewPager.adapter?.itemCount?.dec()) {
                        nextButton.text = getString(R.string.start_activation)
                        skipButton.gone()
                    } else {
                        nextButton.text = getString(R.string.next)
                        skipButton.visible()
                    }
                }
            }
            onBoardingViewPager.apply {
                oneShotPreDrawListener = doOnPreDraw {
                    onBoardingViewPager.setCurrentItem(0, false)
                }
                registerOnPageChangeCallback(requireNotNull(onPageChangeCallback))
            }
        }
    }

    private fun onNextButtonClicked() {
        with(binding) {
            val currentPageIndex = onBoardingViewPager.currentItem
            val lastPageIndex = onBoardingViewPager.adapter?.itemCount?.dec()

            if (currentPageIndex == lastPageIndex) {
                onBoardingViewModel.onSkipButtonClicked()
            } else {
                onBoardingViewPager.currentItem = currentPageIndex + 1
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
        findNavController().navigate(
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
        onPageChangeCallback?.also {
            binding.onBoardingViewPager.unregisterOnPageChangeCallback(it)
        }
        onPageChangeCallback = null
        oneShotPreDrawListener?.removeListener()
        oneShotPreDrawListener = null
        super.onDestroyView()
        _binding = null
    }
}
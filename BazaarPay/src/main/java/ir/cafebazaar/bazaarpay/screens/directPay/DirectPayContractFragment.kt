package ir.cafebazaar.bazaarpay.screens.directPay

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
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractResponse
import ir.cafebazaar.bazaarpay.databinding.FragmentDirectPayContractBinding
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.navigateSafe
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.getErrorViewBasedOnErrorModel
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarPayImageLoader

class DirectPayContractFragment : Fragment() {

    private var _binding: FragmentDirectPayContractBinding? = null
    private val binding: FragmentDirectPayContractBinding
        get() = requireNotNull(_binding)

    private val viewModel by viewModels<DirectPayContractViewModel>()

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
        initViews()
        registerObservers()
        loadData()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    private fun initViews() {
        binding.backButton.setSafeOnClickListener { handleBackPress() }
        binding.cancelButton.setSafeOnClickListener { handleBackPress() }
    }

    private fun registerObservers() {
        viewModel.contractLiveData.observe(viewLifecycleOwner, ::onContractLoaded)
    }

    private fun onContractLoaded(resource: Resource<DirectPayContractResponse>?) {
        resource?.let {
            when (it.resourceState) {
                ResourceState.Loading -> {
                    hideErrorView()
                    with(binding) {
                        contentGroup.gone()
                        loading.visible()
                    }
                }

                ResourceState.Success -> {
                    hideErrorView()
                    with(binding) {
                        contentGroup.visible()
                        loading.gone()
                    }
                    showContract(it.data)
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

    private fun showContract(data: DirectPayContractResponse?) {
        data ?: return

        BazaarPayImageLoader.loadImage(
            imageView = binding.imageMerchant,
            imageURI = data.merchantLogo
        )
    }

    private fun hideErrorView() {
        binding.errorView.gone()
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
        loadData()
    }

    private fun loadData() {
        viewModel.loadData(DirectPayContractFragmentArgs.fromBundle(requireArguments()).contractToken)
    }

    private fun onLoginClicked() {
        findNavController().navigateSafe(
            R.id.open_signin
        )
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
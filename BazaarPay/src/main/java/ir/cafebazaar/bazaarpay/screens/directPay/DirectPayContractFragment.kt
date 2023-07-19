package ir.cafebazaar.bazaarpay.screens.directPay

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ir.cafebazaar.bazaarpay.FinishCallbacks
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.ServiceLocator.DIRECT_PAY_CONTRACT_TOKEN
import ir.cafebazaar.bazaarpay.ServiceLocator.DIRECT_PAY_MERCHANT_MESSAGE
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractAction
import ir.cafebazaar.bazaarpay.data.directPay.model.DirectPayContractResponse
import ir.cafebazaar.bazaarpay.databinding.FragmentDirectPayContractBinding
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.navigateSafe
import ir.cafebazaar.bazaarpay.extensions.persianDigitsIfPersian
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.screens.logout.LogoutFragmentDirections
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.getErrorViewBasedOnErrorModel
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarPayImageLoader
import java.util.Locale

class DirectPayContractFragment : Fragment() {

    private var _binding: FragmentDirectPayContractBinding? = null
    private val binding: FragmentDirectPayContractBinding
        get() = requireNotNull(_binding)

    private val viewModel by viewModels<DirectPayContractViewModel>()

    private val merchantMessage: String? by lazy {
        ServiceLocator.getOrNull(DIRECT_PAY_MERCHANT_MESSAGE)
    }
    private val contractToken: String by lazy { ServiceLocator.get(DIRECT_PAY_CONTRACT_TOKEN) }

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
        binding.cancelButton.setSafeOnClickListener {
            viewModel.finalizeContract(
                contractToken = contractToken,
                action = DirectPayContractAction.Decline
            )
        }
        binding.approveButton.setSafeOnClickListener {
            viewModel.finalizeContract(
                contractToken = contractToken,
                action = DirectPayContractAction.Confirm
            )
        }
        binding.changeAccountLayout.changeAccountAction.setSafeOnClickListener {
            findNavController().navigate(LogoutFragmentDirections.openLogout())
        }
    }

    private fun setAccountData(phone: String?) {
        with(binding) {
            if (phone.isNullOrBlank()) {
                changeAccountLayout.changeAccountBox.gone()
            } else {
                changeAccountLayout.changeAccountBox.visible()
                changeAccountLayout.userAccountPhone.text =
                    phone.persianDigitsIfPersian(Locale.getDefault())
            }
        }
    }

    private fun registerObservers() {
        viewModel.contractLiveData.observe(viewLifecycleOwner, ::onContractLoaded)
        viewModel.contractActionLiveData.observe(viewLifecycleOwner, ::onFinalizeContractResponse)
        viewModel.accountInfoLiveData.observe(viewLifecycleOwner, ::setAccountData)
    }

    private fun onFinalizeContractResponse(result: Pair<Resource<Unit>, DirectPayContractAction>) {
        val (response, action) = result
        if (action == DirectPayContractAction.Decline) {
            binding.cancelButton.isLoading = response.isLoading
            binding.approveButton.isLoading = false
        } else {
            binding.cancelButton.isLoading = false
            binding.approveButton.isLoading = response.isLoading
        }

        when (response.resourceState) {
            ResourceState.Success -> {
                if (action == DirectPayContractAction.Confirm) {
                    finishCallbacks?.onSuccess()
                } else {
                    finishCallbacks?.onCanceled()
                }
            }

            ResourceState.Error -> {
                Toast.makeText(requireContext(), response.failure?.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
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
                    showErrorView(it.failure)
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

        binding.txtDescription.text = data.description
        binding.txtMerchantDescription.text = data.description
        BazaarPayImageLoader.loadImage(
            imageView = binding.imageMerchantInfo,
            imageURI = data.merchantLogo
        )
        binding.txtTitle.text = getString(R.string.bazaarpay_direct_pay_title, data.merchantName)

        binding.merchantMessageContainer.isVisible = merchantMessage.isNullOrEmpty().not()
        if (merchantMessage.isNullOrEmpty().not()) {
            binding.txtMerchantTitle.text = getString(
                R.string.bazaarpay_merchant_message,
                data.merchantName
            )
            binding.txtMerchantDescription.text = merchantMessage
        }
    }

    private fun hideErrorView() {
        binding.errorView.gone()
    }

    private fun showErrorView(errorModel: ErrorModel?) {
        errorModel ?: return
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
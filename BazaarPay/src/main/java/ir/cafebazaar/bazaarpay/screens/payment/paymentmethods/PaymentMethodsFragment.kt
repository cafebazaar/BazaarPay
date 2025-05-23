package ir.cafebazaar.bazaarpay.screens.payment.paymentmethods

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import ir.cafebazaar.bazaarpay.FinishCallbacks
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.base.BaseFragment
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethod
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodItems
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo
import ir.cafebazaar.bazaarpay.data.payment.models.pay.PayResult
import ir.cafebazaar.bazaarpay.databinding.FragmentPaymentOptionsBinding
import ir.cafebazaar.bazaarpay.extensions.getReadableErrorMessage
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.navigateSafe
import ir.cafebazaar.bazaarpay.extensions.openUrl
import ir.cafebazaar.bazaarpay.extensions.persianDigitsIfPersian
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.toastMessage
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.PaymentFlowState
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.screens.logout.LogoutFragmentDirections
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsAdapter.Companion.DEFAULT_SELECTED_OPTION
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.getErrorViewBasedOnErrorModel
import java.util.Locale

internal class PaymentMethodsFragment : BaseFragment(SCREEN_NAME), PaymentMethodsClickListener {

    private val viewModel: PaymentMethodsViewModel by viewModels()

    private var _binding: FragmentPaymentOptionsBinding? = null
    private val binding: FragmentPaymentOptionsBinding
        get() = requireNotNull(_binding)

    private var paymentMethodsAdapter: PaymentMethodsAdapter? = null
    private var finishCallbacks: FinishCallbacks? = null

    private var savedSelectedItemPosition: Int? = null

    private val args by lazy { PaymentMethodsFragmentArgs.fromBundle(requireArguments()) }

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
        _binding = inflater.bindWithRTLSupport(FragmentPaymentOptionsBinding::inflate, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(savedInstanceState)
        initUI()
        loadData()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    private fun initData(savedInstanceState: Bundle?) {

        savedSelectedItemPosition = savedInstanceState?.getInt(KEY_SELECTED_ITEM_POSITION)
        observeOnPaymentViewModel()
    }

    private fun observeOnPaymentViewModel() {
        with(viewModel) {
            getPaymentMethodsStateData().observe(viewLifecycleOwner, ::handlePaymentMethodsStates)
            getPayStateData().observe(viewLifecycleOwner, ::handlePayStates)
            getMerchantInfoStateData().observe(viewLifecycleOwner, ::handleMerchantInfoStates)
            paymentMethodViewLoaderLiveData.observe(viewLifecycleOwner, ::loadPaymentOptionView)
            navigationLiveData.observe(viewLifecycleOwner, ::handleNavigation)
            deepLinkLiveData.observe(viewLifecycleOwner, ::handleDeepLink)
            accountInfoLiveData.observe(viewLifecycleOwner, ::setAccountData)
        }
    }

    private fun handleDeepLink(deepLink: String) {
        requireContext().openUrl(deepLink)
    }

    private fun initUI() {
        with(binding) {
            paymentOptionClose.setSafeOnClickListener {
                handleBackPress()
            }

            initPaymentGatewayRecyclerView()

            changeAccountLayout.changeAccountAction.setSafeOnClickListener {
                Analytics.sendClickEvent(where, what = CHANGE_ACCOUNT)
                handleNavigation(LogoutFragmentDirections.openLogout())
            }
        }
    }

    private fun initPaymentGatewayRecyclerView() {
        paymentMethodsAdapter = PaymentMethodsAdapter(this@PaymentMethodsFragment)
        binding.paymentGatewaysRecyclerView.apply {
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = paymentMethodsAdapter
        }
    }

    private fun handleBackPress() {
        finishCallbacks?.onCanceled()
    }

    private fun loadData() {
        val hasLoaded = requireArguments().getBoolean(KEY_DEFAULT_METHOD_LOADED, false)
        if (hasLoaded.not()) {
            requireArguments().putBoolean(KEY_DEFAULT_METHOD_LOADED, true)
        }

        viewModel.loadData(
            defaultMethod = args.defaultMethod,
            shouldLoadDefaultMethod = hasLoaded.not(),
        )
    }

    private fun populatePaymentOptions(paymentMethods: PaymentMethodItems) {
        binding.payButton.setSafeOnClickListener { onPayButtonClicked() }
        notifyPaymentOptionAdapter(paymentMethods)
        setPaymentOptionRecyclerViewScrollPosition()
    }

    private fun setupMerchantInfoViews(merchantInfo: MerchantInfo) {
        with(binding.viewMerchantInfo) {
            setMerchantIcon(merchantInfo.logoUrl)
            setMerchantName(merchantInfo.accountName)
        }
    }

    private fun notifyPaymentOptionAdapter(paymentMethods: List<PaymentMethod>) {
        requireNotNull(paymentMethodsAdapter).run {
            items.clear()
            items.addAll(paymentMethods)
            notifyDataSetChanged()
            setSelectedOptionAccessibility()
        }
    }

    private fun setPaymentOptionRecyclerViewScrollPosition() {
        with(binding.paymentGatewaysRecyclerView) {
            post {
                val selectedItem = getSavedSelectedItemPosition()
                onItemClick(selectedItem, isActionByUser = false)
                children.firstOrNull()?.post {
                    scrollToPosition(selectedItem)
                }
            }
        }
    }

    private fun getSavedSelectedItemPosition(): Int {
        var selectedItem = savedSelectedItemPosition ?: DEFAULT_SELECTED_OPTION

        paymentMethodsAdapter?.itemCount?.let { itemCount ->
            if (selectedItem >= itemCount) {
                selectedItem = DEFAULT_SELECTED_OPTION
            }
        }
        return selectedItem
    }

    override fun onItemClick(position: Int, isActionByUser: Boolean) {
        if (!isAdded) {
            return
        }

        paymentMethodsAdapter?.setSelectedPosition(position)
        viewModel.onPaymentOptionClicked(position, isActionByUser)
        binding.paymentGatewaysRecyclerView.scrollToPosition(position)
        setSelectedOptionAccessibility()
    }

    private fun setSelectedOptionAccessibility() {
        binding.selectOptionTitle.contentDescription = getString(
            R.string.bazaarpay_content_description_payment_option_selection,
            paymentMethodsAdapter?.getSelectedOptionTitle(),
        )
    }

    private fun loadPaymentOptionView(viewLoader: PaymentMethodViewLoader) {
        with(binding) {
            viewMerchantInfo.setPrice(
                viewLoader.price
            )

            payButton.text = getString(viewLoader.payButton)
            payButton.isEnabled = viewLoader.enabled

            if (viewLoader.subDescription.isNullOrEmpty()) {
                paymentOptionInfo?.gone()
            } else {
                paymentOptionInfo?.apply {
                    visible()
                    text = viewLoader.subDescription
                }
            }
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

    private fun handleNavigation(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }

    private fun onPayButtonClicked() {
        viewModel.onPayButtonClicked(
            requireNotNull(paymentMethodsAdapter).selectedPosition
        )
    }

    private fun handleMerchantInfoStates(resource: Resource<MerchantInfo>?) {
        resource?.let {
            when (it.resourceState) {
                PaymentFlowState.MerchantInfo -> {
                    setupMerchantInfoViews(resource.data as MerchantInfo)
                }
            }
        }
    }

    private fun handlePaymentMethodsStates(resource: Resource<PaymentMethodsInfo>?) {
        resource?.let {
            when (it.resourceState) {
                ResourceState.Loading -> {
                    showLoadingContainer()
                }

                PaymentFlowState.PaymentMethodsInfo -> {
                    handlePaymentMethods((resource.data as PaymentMethodsInfo).paymentMethods)
                }

                ResourceState.Error -> {
                    it.failure?.let { failure ->
                        handleErrorState(failure)
                    }
                }

                else -> {}
            }
        }
    }

    private fun handlePayStates(resource: Resource<PayResult>?) {
        resource?.let {
            binding.payButton.isLoading = it.resourceState == ResourceState.Loading
            when (it.resourceState) {
                ResourceState.Success -> {
                }

                ResourceState.Error -> {
                    toastMessage(requireContext().getReadableErrorMessage(it.failure))
                }
            }
        }
    }

    private fun handlePaymentMethods(data: PaymentMethodItems) {
        try {
            showContentContainer()
            populatePaymentOptions(paymentMethods = data)
        } catch (e: NullPointerException) {
            Throwable("Something is not supposed to be null", e)
            handleErrorState(ErrorModel.UnExpected)
        }
    }

    private fun showLoadingContainer() {
        with(binding) {
            contentContainer.gone()
            loadingContainer.visible()
            hideErrorView()
        }
    }

    private fun showErrorContainer(errorModel: ErrorModel) {
        with(binding) {
            contentContainer.gone()
            loadingContainer.gone()
            showErrorView(errorModel)
        }
    }

    private fun showContentContainer() {
        with(binding) {
            contentContainer.visible()
            loadingContainer.gone()
            hideErrorView()
        }
    }

    private fun handleErrorState(errorModel: ErrorModel) {
        showErrorContainer(errorModel)
    }

    override fun onDetach() {
        finishCallbacks = null
        super.onDetach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        paymentMethodsAdapter?.let {
            outState.putInt(KEY_SELECTED_ITEM_POSITION, it.selectedPosition)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun onLoginClicked() {
        findNavController().navigateSafe(
            R.id.open_signin
        )
    }

    private fun hideErrorView() {
        binding.errorView.gone()
    }

    companion object {

        internal const val SCREEN_NAME = "PaymentMethods"
        private const val KEY_SELECTED_ITEM_POSITION = "selectedItemPos"
        private const val KEY_DEFAULT_METHOD_LOADED = "defaultMethodLoaded"

        //analytics
        internal val CHANGE_ACCOUNT = "changeAccount"
        internal val CLICK_PAY_PUTTON = "clickPayButton"
        internal val SELECTED_METHODE = "selectedMethode"
        internal val PAYMENT_METHODES = "paymentMethodes"
    }
}
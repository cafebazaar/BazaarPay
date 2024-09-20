package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.main.BazaarPayActivity
import ir.cafebazaar.bazaarpay.FinishCallbacks
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs
import ir.cafebazaar.bazaarpay.base.BaseFragment
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.DynamicCreditOption
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.Option
import ir.cafebazaar.bazaarpay.databinding.FragmentPaymentDynamicCreditBinding
import ir.cafebazaar.bazaarpay.extensions.fixAccessibility
import ir.cafebazaar.bazaarpay.extensions.getReadableErrorMessage
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.hideKeyboard
import ir.cafebazaar.bazaarpay.extensions.moveCursorToEnd
import ir.cafebazaar.bazaarpay.extensions.navigateSafe
import ir.cafebazaar.bazaarpay.extensions.openUrl
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.setValueIfNotNullOrEmpty
import ir.cafebazaar.bazaarpay.extensions.toastMessage
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.getErrorViewBasedOnErrorModel

internal class PaymentDynamicCreditFragment : BaseFragment(SCREEN_NAME) {

    private var dealerArgs: DynamicCreditOptionDealerArg? = null

    private val dynamicCreditViewModel: DynamicCreditViewModel by viewModels()

    private var textWatcher: TextWatcher? = null

    private var _binding: FragmentPaymentDynamicCreditBinding? = null
    private val binding: FragmentPaymentDynamicCreditBinding
        get() = requireNotNull(_binding)

    private var finishCallbacks: FinishCallbacks? = null

    private val activityArgs: BazaarPayActivityArgs? by lazy {
        requireActivity().intent.getParcelableExtra(
            BazaarPayActivity.BAZAARPAY_ACTIVITY_ARGS
        )
    }

    private val args by lazy { PaymentDynamicCreditFragmentArgs.fromBundle(requireArguments()) }

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
        _binding = inflater.bindWithRTLSupport(
            FragmentPaymentDynamicCreditBinding::inflate,
            container
        )
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(args) {
            dealerArgs = dealer
            dynamicCreditViewModel.initArgs(creditOptions)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createDynamicCreditViewModel()
        setViewListeners()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    private fun createDynamicCreditViewModel() {
        with(dynamicCreditViewModel) {
            editTextValueLiveData.observe(viewLifecycleOwner) {
                binding.payButton.isEnabled = it.isNullOrEmpty().not()
                with(binding.priceEditText) {
                    removeTextChangedListener(textWatcher)
                    setText(it)
                    moveCursorToEnd()
                    addTextChangedListener(textWatcher)
                }
            }

            itemChangedLiveData.observe(viewLifecycleOwner) {
                binding.dynamicCreditRecyclerView.adapter?.notifyItemChanged(it)
            }

            errorLiveData.observe(viewLifecycleOwner) {
                val errorMessage = requireContext().getString(it.first, it.second)
                toastMessage(errorMessage.fixAccessibility())
            }

            actionLiveData.observe(viewLifecycleOwner, ::handleActionState)
            navigationLiveData.observe(viewLifecycleOwner, ::handleNavigation)
            dynamicCreditLiveData.observe(viewLifecycleOwner, ::handleDynamicCreditState)
        }
    }

    private fun handleActionState(resource: Resource<String>) {
        binding.payButton.isLoading = resource.resourceState is ResourceState.Loading
        when (resource.resourceState) {
            ResourceState.Error -> {
                toastMessage(requireContext().getReadableErrorMessage(resource.failure))
            }

            ResourceState.Success -> {
                resource.data?.let {
                    requireContext().openUrl(it)
                }
            }
        }
    }

    private fun handleNavigation(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }

    private fun handleDynamicCreditState(resource: Resource<DynamicCreditOption>) {
        when (resource.resourceState) {
            ResourceState.Error -> {
                handleErrorState(resource.failure)
            }

            ResourceState.Loading -> {
                showLoadingContainer()
            }

            ResourceState.Success -> {
                showContentContainer()
                initView(resource.data)
            }

            else -> {
                IllegalStateException(
                    "Invalid state of handleDataState:${resource.resourceState}"
                )
            }
        }
    }

    private fun showLoadingContainer() {
        with(binding) {
            contentContainer.gone()
            hideErrorView()
            loadingContainer.visible()
        }
    }

    private fun showContentContainer() {
        with(binding) {
            loadingContainer.gone()
            hideErrorView()
            contentContainer.visible()
        }
    }

    private fun showErrorContainer(errorModel: ErrorModel) {
        with(binding) {
            contentContainer.gone()
            loadingContainer.gone()
            showErrorView(errorModel)
        }
    }

    private fun handleErrorState(errorModel: ErrorModel?) {
        if (errorModel is ErrorModel.NetworkConnection) {
            showErrorContainer(errorModel)
        } else {
            binding.dynamicCreditWarning.text = errorModel?.message
        }
    }

    private fun initView(creditOptionsArgs: DynamicCreditOption?) {
        creditOptionsArgs ?: return
        setDealerInfo(dealerArgs)
        setCreditOptions(creditOptionsArgs)
        with(creditOptionsArgs) {
            if (description == binding.dynamicCreditPayOrEnterTitle.text) {
                binding.dynamicCreditSubTitle.gone()
                val padding =
                    binding.root.resources.getDimension(R.dimen.bazaarpay_default_margin_double)
                binding.dynamicCreditPayOrEnterTitle.setPadding(0, padding.toInt(), 0, 0)
            } else {
                binding.dynamicCreditSubTitle.setValueIfNotNullOrEmpty(description)
            }
        }
    }

    private fun setDealerInfo(dealer: DynamicCreditOptionDealerArg?) {
        binding.merchantInfo.isVisible = dealer != null
        dealer ?: return
        with(binding.merchantInfo) {
            setMerchantName(dealer.name)
            setPrice(dealer.priceString)
            setMerchantInfo(dealer.info)
            setMerchantIcon(dealer.iconUrl)
        }
    }

    private fun setCreditOptions(creditOptionsArgs: DynamicCreditOption) {
        with(creditOptionsArgs) {
            binding.dynamicCreditBalance.setBalance(userBalance, userBalanceString)
            if (options.isEmpty()) {
                binding.dynamicCreditRecyclerView.gone()
                binding.dynamicCreditPayOrEnterTitle.text =
                    getString(R.string.bazaarpay_enter_dynamic_credit_title)
            } else {
                initRecyclerView(options)
            }
        }
    }

    private fun initRecyclerView(creditOptions: List<Option>) {
        with(binding.dynamicCreditRecyclerView) {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            itemAnimator = null
            setHasFixedSize(true)
            adapter = DynamicCreditOptionAdapter(creditOptions) { position ->
                dynamicCreditViewModel.onDynamicItemClicked(position)
            }
            post {
                scrollToPosition(0)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setViewListeners() {
        with(binding) {

            dynamicCreditBack.setSafeOnClickListener {
                handleBackPress()
            }

            payButton.setSafeOnClickListener {
                val type = if (activityArgs is BazaarPayActivityArgs.IncreaseBalance) {
                    IncreaseCreditType.INCREASE
                } else {
                    IncreaseCreditType.PAY
                }
                dynamicCreditViewModel.onPayButtonClicked(
                    binding.priceEditText.text.toString(),
                    type
                )
            }
            textWatcher = priceEditText.doOnTextChanged { text, _, _, _ ->
                dynamicCreditViewModel.onTextChanged(text.toString())
            }
            priceEditText.setOnEditorActionListener { _, actionId, _ ->
                if (priceEditText.text.isNullOrEmpty()
                        .not() && actionId == EditorInfo.IME_ACTION_DONE
                ) {
                    priceEditText.clearFocus()
                    payButton.requestFocus()
                    payButton.performAccessibilityAction(
                        AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,
                        null
                    )
                    payButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
                }
                false
            }
            priceEditText.setOnTouchListener { view, event ->
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        view.postDelayed(
                            {
                                binding.creditScrollView.fullScroll(View.FOCUS_DOWN)
                                binding.priceEditText.requestFocus()
                            },
                            SCROLL_DELAY
                        )
                    }
                }
                false
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onDestroyView() {
        binding.priceEditText.setOnTouchListener(null)
        textWatcher?.let { binding.priceEditText.removeTextChangedListener(it) }
        textWatcher = null
        super.onDestroyView()
        _binding = null
    }

    private fun handleBackPress() {
        hideKeyboard()
        dynamicCreditViewModel.onBackClicked()
        if (activityArgs is BazaarPayActivityArgs.IncreaseBalance) {
            finishCallbacks?.onCanceled()
        } else {
            findNavController().popBackStack()
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
        dynamicCreditViewModel.onRetryClick(args.creditOptions)
    }

    private fun onLoginClicked() {
        findNavController().navigateSafe(
            R.id.open_signin
        )
    }

    private fun hideErrorView() {
        binding.errorView.gone()
    }

    override fun onDetach() {
        finishCallbacks = null
        super.onDetach()
    }

    companion object {

        private const val SCROLL_DELAY = 300L
        internal const val SCREEN_NAME = "PaymentDynamicCredit"
        internal val CLICK_AMOUNT_OPTION = "clickAmountOption"
        internal val AMOUNT_OPTION = "amountOption"
    }
}
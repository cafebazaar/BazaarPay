package ir.cafebazaar.bazaarpay.screens.payment.increasecredit

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.base.BaseFragment
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.DynamicCreditOption
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.Option
import ir.cafebazaar.bazaarpay.databinding.FragmentPaymentDynamicCreditBinding
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

    private var _creditOptionsArgs: DynamicCreditOption? = null
    private val creditOptionsArgs: DynamicCreditOption
        get() = requireNotNull(_creditOptionsArgs)

    private var _dealerArgs: DynamicCreditOptionDealerArg? = null
    private val dealerArgs: DynamicCreditOptionDealerArg
        get() = requireNotNull(_dealerArgs)

    private val dynamicCreditViewModel: DynamicCreditViewModel by viewModels()

    private var textWatcher: TextWatcher? = null

    private var _binding: FragmentPaymentDynamicCreditBinding? = null
    private val binding: FragmentPaymentDynamicCreditBinding
        get() = requireNotNull(_binding)

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
        _creditOptionsArgs =
            PaymentDynamicCreditFragmentArgs.fromBundle(requireArguments()).creditOptions
        _dealerArgs =
            PaymentDynamicCreditFragmentArgs.fromBundle(requireArguments()).dealer
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
                toastMessage(errorMessage)
            }

            actionLiveData.observe(viewLifecycleOwner) {
                handleActionState(it)
            }

            dynamicCreditLiveData.observe(viewLifecycleOwner) {
                handleDynamicCreditState(it)
            }

            loadData()
        }
    }

    private fun loadData() {
        dynamicCreditViewModel.onViewCreated(creditOptionsArgs)
    }

    private fun handleActionState(resource: Resource<String>) {
        binding.payButton.isLoading = resource.resourceState is ResourceState.Loading
        when (resource.resourceState) {
            ResourceState.Error -> {
                toastMessage(requireContext().getReadableErrorMessage(resource.failure))
            }

            ResourceState.Success -> {
                resource.data?.let { requireContext().openUrl(it) }
            }
        }
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
                _creditOptionsArgs = requireNotNull(resource.data)
                initView()
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

    private fun initView() {
        setDealerInfo()
        setCreditOptions()
        with(creditOptionsArgs) {
            binding.dynamicCreditSubTitle.setValueIfNotNullOrEmpty(description)
        }
    }

    private fun setDealerInfo() {
        with(binding.merchantInfo) {
            setMerchantName(dealerArgs.name)
            setPrice(dealerArgs.priceString)
            setMerchantInfo(dealerArgs.info)
            setMerchantIcon(dealerArgs.iconUrl)
        }
    }

    private fun setCreditOptions() {
        with(creditOptionsArgs) {
            binding.dynamicCreditBalance.setBalance(userBalance, userBalanceString)
            initRecyclerView(creditOptionsArgs.options)
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
                dynamicCreditViewModel.onPayButtonClicked(binding.priceEditText.text.toString())
            }
            textWatcher = priceEditText.doOnTextChanged { text, _, _, _ ->
                dynamicCreditViewModel.onTextChanged(text.toString())
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
        findNavController().popBackStack()
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

        private const val SCROLL_DELAY = 300L
        private const val SCREEN_NAME = "PaymentDynamicCredit"
    }
}
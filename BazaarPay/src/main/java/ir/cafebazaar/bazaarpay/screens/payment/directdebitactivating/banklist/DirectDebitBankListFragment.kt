package ir.cafebazaar.bazaarpay.screens.payment.directdebitactivating.banklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.base.BaseFragment
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.databinding.FragmentDirectDebitBankListBinding
import ir.cafebazaar.bazaarpay.extensions.getReadableErrorMessage
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.navigateSafe
import ir.cafebazaar.bazaarpay.extensions.openUrl
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.getErrorViewBasedOnErrorModel

internal class DirectDebitBankListFragment : BaseFragment(SCREEN_NAME) {

    private var adapter: BankListAdapter? = null

    private var _binding: FragmentDirectDebitBankListBinding? = null
    private val binding: FragmentDirectDebitBankListBinding
        get() = requireNotNull(_binding)

    private val viewModel: DirectDebitBankListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflater.bindWithRTLSupport(
            FragmentDirectDebitBankListBinding::inflate,
            container
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentDirectDebitBankListBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            titleTextView.text = getString(R.string.bazaarpay_direct_debit_bank_list)
            backButton.setSafeOnClickListener {
                findNavController().popBackStack()
            }
            actionButton.apply {
                isEnabled = false
                setSafeOnClickListener {
                    viewModel.onRegisterClicked(
                        DirectDebitBankListFragmentArgs.fromBundle(requireArguments()).nationalId
                    )
                }
            }
        }
        viewModel.loadData()
        observeViewModel()
        initRecycler()
    }

    private fun initRecycler() {

        adapter = BankListAdapter()

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@DirectDebitBankListFragment.adapter
            itemAnimator?.changeDuration = 0
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                requireContext(),
                R.anim.recycler_view_fall_down
            )
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            enableActionButtonStateLiveData.observe(viewLifecycleOwner) { isEnabled ->
                binding.actionButton.isEnabled = isEnabled
            }
            registerDirectDebitLiveData.observe(viewLifecycleOwner) { resource ->
                binding.actionButton.isLoading = resource.isLoading
                when (resource.resourceState) {
                    ResourceState.Error -> {
                        showReadableErrorMessage(resource.failure)
                    }

                    ResourceState.Success -> {
                        openUrlWithResourceData(resource.data)
                    }
                }
            }
            dataLiveData.observe(viewLifecycleOwner, ::handleData)
            notifyLiveData.observe(viewLifecycleOwner, ::handleNotify)
            navigationLiveData.observe(viewLifecycleOwner, ::handleNavigation)
        }
    }

    private fun showReadableErrorMessage(errorModel: ErrorModel?) {
        if (isAdded) {
            val message = requireContext().getReadableErrorMessage(errorModel)
            binding.root.let {
                Snackbar.make(it, message, Snackbar.LENGTH_LONG).run { show() }
            }
        }
    }

    private fun openUrlWithResourceData(url: String?) {
        url?.let { requireContext().openUrl(it) }
    }

    private fun handleNavigation(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

    private fun handleNotify(index: Int) {
        adapter?.notifyItemChanged(index)
    }

    private fun handleData(resource: Resource<List<BankList>>) {
        if (resource.isError) {
            resource.failure?.let { failure ->
                showErrorView(failure)
            }
        } else {
            hideErrorView()
        }

        with(binding) {
            emptyView.isVisible = resource.isEmpty
            loading.isVisible = resource.isLoading
            recyclerView.isVisible = resource.isSuccess
        }

        if (resource.isSuccess && resource.data != null) {
            adapter?.setItems(resource.data)
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
        binding.actionButton.isVisible = false
    }

    private fun onRetryClicked() {
        viewModel.loadData()
    }

    private fun onLoginClicked() {
        findNavController().navigateSafe(
            R.id.open_signin
        )
    }

    private fun hideErrorView() {
        binding.errorView.gone()
        binding.actionButton.isVisible = true
    }

    private companion object {

        const val SCREEN_NAME = "DirectDebitBankList"
    }
}
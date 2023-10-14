package ir.cafebazaar.bazaarpay.sample

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.launcher.normal.BazaarPayOptions
import ir.cafebazaar.bazaarpay.launcher.normal.StartBazaarPay
import ir.cafebazaar.bazaarpay.sample.databinding.FragmentPaymentBinding
import ir.cafebazaar.bazaarpay.R as BazaarPayR

class SamplePaymentFragment : Fragment(R.layout.fragment_payment) {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPaymentBinding.bind(view)
        binding.paymentButton.setSafeOnClickListener {
            startPayment()
        }

        binding.darkModeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

        }
    }

    private fun startPayment() {
        val options = BazaarPayOptions
            .paymentUrl(paymentURL = binding.paymentUrlInput.text.toString())
            .phoneNumber(phoneNumber = binding.phoneNumberInput.text.toString())
            .authToken(authToken = binding.tokenInput.text.toString())
            .build()
        bazaarPayLauncher.launch(options)
    }

    private val bazaarPayLauncher = registerForActivityResult(StartBazaarPay()) { isSuccessful ->
        if (isSuccessful) {
            showPaymentResult(R.string.message_successful_payment)
        } else {
            showPaymentResult(R.string.message_payment_cancelled, isError = true)
        }
    }

    private fun showPaymentResult(
        @StringRes messageRes: Int,
        isError: Boolean = false
    ) {
        binding.paymentResult.setText(messageRes)
        val colorRes = if (isError) {
            BazaarPayR.color.bazaarpay_error_primary
        } else {
            BazaarPayR.color.bazaarpay_app_brand_primary
        }
        binding.paymentResult.setTextColor(
            ContextCompat.getColor(requireActivity(), colorRes)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

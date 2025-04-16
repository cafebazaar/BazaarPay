package ir.cafebazaar.bazaarpay.sample

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import ir.cafebazaar.bazaarpay.commit
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.launcher.normal.BazaarPayOptions
import ir.cafebazaar.bazaarpay.launcher.normal.PaymentMethod
import ir.cafebazaar.bazaarpay.launcher.normal.StartBazaarPay
import ir.cafebazaar.bazaarpay.sample.balance.BalanceSampleActivity
import ir.cafebazaar.bazaarpay.sample.databinding.ActivityPaymentBinding
import ir.cafebazaar.bazaarpay.trace
import kotlinx.coroutines.launch
import ir.cafebazaar.bazaarpay.R as BazaarPayR

class SamplePaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var paymentURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerClickListeners(binding)

        savedInstanceState?.let {
            paymentURL = savedInstanceState.getString(KEY_PAYMENT_URL, "")
        }
    }

    private fun registerClickListeners(binding: ActivityPaymentBinding) {
        binding.paymentButton.setSafeOnClickListener {
            paymentURL = binding.paymentUrlInput.text.toString()
            startPayment()
        }

        binding.traceButton.setSafeOnClickListener {
            paymentURL = binding.paymentUrlInput.text.toString()
            startTracing()
        }

        binding.paymentFragmentButton.setSafeOnClickListener {
            val intent = Intent(this, SampleFragmentContainer::class.java)
            startActivity(intent)
        }
        binding.initCheckoutActivityButton.setSafeOnClickListener {
            val intent = Intent(this, SampleInitCheckoutActivity::class.java)
            startActivity(intent)
        }

        binding.balanceButton.setSafeOnClickListener {
            val intent = Intent(this, BalanceSampleActivity::class.java)
            startActivity(intent)
        }

        binding.directPayContractActivityButton.setSafeOnClickListener {
            val intent = Intent(this, SampleDirectPayContractActivity::class.java)
            startActivity(intent)
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
        val paymentMethod = if (binding.forceIncreaseBalanceCheckbox.isChecked) {
            PaymentMethod.INCREASE_BALANCE
        } else null

        val options = BazaarPayOptions
            .paymentUrl(paymentURL = paymentURL)
            .phoneNumber(phoneNumber = binding.phoneNumberInput.text.toString())
            .authToken(authToken = binding.tokenInput.text.toString())
            .accessibility(accessibility = binding.accessibilityCheckbox.isChecked)
            .paymentMethod(paymentMethod = paymentMethod)
            .build()
        bazaarPayLauncher.launch(options)
    }

    private val bazaarPayLauncher = registerForActivityResult(StartBazaarPay()) { isSuccessful ->
        if (isSuccessful) {
            showPaymentResult(R.string.message_successful_payment)
            if (binding.commitPaymentCheckbox.isChecked) {
                commitPaymentURL()
            }
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
            ContextCompat.getColor(this, colorRes)
        )
    }

    private fun commitPaymentURL() {
        lifecycleScope.launch {
            commit(
                paymentURL = paymentURL,
                context = this@SamplePaymentActivity,
                onSuccess = {
                    // Successfully committed!
                },
                onFailure = {
                    // You can use ErrorModel (it) to handle the error
                }
            )
        }
    }

    private fun startTracing() {
        lifecycleScope.launch {
            trace(
                paymentURL = paymentURL,
                context = this@SamplePaymentActivity,
                onSuccess = {
                    binding.traceResult.text = it.toString()
                },
                onFailure = {
                    binding.traceResult.text = it.message
                }
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PAYMENT_URL, paymentURL)
    }

    companion object {

        const val KEY_PAYMENT_URL = "paymentURL"
    }
}

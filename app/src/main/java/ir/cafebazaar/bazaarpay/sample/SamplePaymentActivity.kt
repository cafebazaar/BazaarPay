package ir.cafebazaar.bazaarpay.sample

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import ir.cafebazaar.bazaarpay.BazaarPayOptions
import ir.cafebazaar.bazaarpay.StartBazaarPay
import ir.cafebazaar.bazaarpay.commit
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.sample.databinding.ActivityPaymentBinding
import ir.cafebazaar.bazaarpay.trace
import kotlinx.coroutines.launch
import ir.cafebazaar.bazaarpay.R as BazaarPayR

class SamplePaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var paymentURL: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerClickListeners(binding)
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
            .paymentUrl(paymentURL = paymentURL)
            .phoneNumber(phoneNumber = binding.phoneNumberInput.text.toString())
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(KEY_PAYMENT_URL, paymentURL)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        paymentURL = savedInstanceState.getString(KEY_PAYMENT_URL, "")
    }

    companion object {

        const val KEY_PAYMENT_URL = "paymentURL"
    }
}

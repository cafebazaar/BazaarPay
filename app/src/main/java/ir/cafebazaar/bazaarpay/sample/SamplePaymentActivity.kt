package ir.cafebazaar.bazaarpay.sample

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var checkoutToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerClickListeners(binding)
    }

    private fun registerClickListeners(binding: ActivityPaymentBinding) {
        binding.paymentButton.setSafeOnClickListener {
            checkoutToken = binding.checkoutTokenInput.text.toString()
            startPayment()
        }

        binding.traceButton.setSafeOnClickListener {
            checkoutToken = binding.checkoutTokenInput.text.toString()
            startTracing()
        }

        binding.paymentFragmentButton.setSafeOnClickListener {
            val intent = Intent(this, SampleFragmentContainer::class.java)
            startActivity(intent)
        }
    }

    private fun startPayment() {
        val options = BazaarPayOptions(
            checkoutToken = checkoutToken,
            phoneNumber = binding.phoneNumberInput.text.toString(),
            isInDarkMode = binding.darkModeCheckbox.isChecked
        )
        bazaarPayLauncher.launch(options)
    }

    private val bazaarPayLauncher = registerForActivityResult(StartBazaarPay()) { isSuccessful ->
        if (isSuccessful) {
            showPaymentResult(R.string.message_successful_payment)
            if (binding.commitPaymentCheckbox.isChecked) {
                commitCheckoutToken()
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

    private fun commitCheckoutToken() {
        lifecycleScope.launch {
            commit(
                checkoutToken,
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
                checkoutToken,
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
        outState.putString(KEY_CHECKOUT_TOKEN, checkoutToken)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        checkoutToken = savedInstanceState.getString(KEY_CHECKOUT_TOKEN, "")
    }

    companion object {
        const val KEY_CHECKOUT_TOKEN = "checkoutToken"
    }
}
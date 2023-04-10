package ir.cafebazaar.bazaarpay.sample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.initCheckout
import ir.cafebazaar.bazaarpay.sample.databinding.ActivitySampleInitCheckoutBinding
import kotlinx.coroutines.launch

class SampleInitCheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySampleInitCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleInitCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerClickListeners(binding)
    }

    private fun registerClickListeners(binding: ActivitySampleInitCheckoutBinding) {
        binding.initCheckoutTokenButton.setSafeOnClickListener {
            initCheckoutToken()
        }
    }

    private fun initCheckoutToken() {
        lifecycleScope.launch {
            initCheckout(
                context = this@SampleInitCheckoutActivity,
                amount = binding.amountEditText.text.toString().toLong(),
                destination = binding.destinationEditText.text.toString(),
                serviceName = binding.serviceNameEditText.text.toString(),
                onSuccess = {
                    binding.checkoutTokenTextView.text = it.checkoutToken
                    binding.redirectUrlTextView.text = it.paymentUrl
                },
                onFailure = {
                    binding.checkoutTokenTextView.text = it.message
                    binding.checkoutTokenTextView.setTextColor(Color.RED)
                }
            )
        }
    }
}

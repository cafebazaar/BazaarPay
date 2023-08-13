package ir.cafebazaar.bazaarpay.sample

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.cafebazaar.bazaarpay.launcher.directPay.DirectPayContractOptions
import ir.cafebazaar.bazaarpay.launcher.directPay.StartDirectPayFinalizeContract
import ir.cafebazaar.bazaarpay.sample.databinding.ActivitySampleDirectPayContractActivityBinding

class SampleDirectPayContractActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySampleDirectPayContractActivityBinding

    private val bazaarPayDirectPayContractLauncher = registerForActivityResult(
        StartDirectPayFinalizeContract()
    ) { isSuccessful ->
        if (isSuccessful) {
            showResult(R.string.message_successful_direct_pay_contract)
        } else {
            showResult(R.string.message_cancelled_direct_pay_contract, isError = true)
        }
    }

    private fun showResult(
        @StringRes messageRes: Int,
        isError: Boolean = false
    ) {
        binding.contractResult.setText(messageRes)
        val colorRes = if (isError) {
            ir.cafebazaar.bazaarpay.R.color.bazaarpay_error_primary
        } else {
            ir.cafebazaar.bazaarpay.R.color.bazaarpay_app_brand_primary
        }
        binding.contractResult.setTextColor(
            ContextCompat.getColor(this, colorRes)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleDirectPayContractActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerClickListeners(binding)
    }

    private fun registerClickListeners(binding: ActivitySampleDirectPayContractActivityBinding) {
        binding.directPayContractButton.setOnClickListener {
            val contractToken = binding.contractTokenInput.text.toString()
            val message = binding.messageInput.text.toString()
            val phone = binding.phoneInput.text.toString()
            startDirectPayContract(contractToken = contractToken, message = message, phone = phone)
        }
    }

    private fun startDirectPayContract(contractToken: String, message: String, phone: String) {
        val options = DirectPayContractOptions(
            contractToken = contractToken,
            message = message,
            phoneNumber = phone
        )
        bazaarPayDirectPayContractLauncher.launch(options)
    }
}
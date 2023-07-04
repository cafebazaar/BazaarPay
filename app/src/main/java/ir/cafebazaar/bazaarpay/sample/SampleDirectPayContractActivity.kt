package ir.cafebazaar.bazaarpay.sample

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import ir.cafebazaar.bazaarpay.directPay.DirectPayContractOptions
import ir.cafebazaar.bazaarpay.directPay.StartDirectPayFinalizeContract
import ir.cafebazaar.bazaarpay.sample.databinding.ActivitySampleDirectPayContractActivityBinding

class SampleDirectPayContractActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySampleDirectPayContractActivityBinding

    private val bazaarPayDirectPayContractLauncher = registerForActivityResult(
        StartDirectPayFinalizeContract()
    ) { isSuccessful ->
        if (isSuccessful) {
            showResult(R.string.message_successful_payment)
        } else {
            showResult(R.string.message_payment_cancelled, isError = true)
        }
    }

    private fun showResult(
        @StringRes messageRes: Int,
        isError: Boolean = false
    ) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
//        binding.paymentResult.setText(messageRes)
//        val colorRes = if (isError) {
//            ir.cafebazaar.bazaarpay.R.color.bazaarpay_error_primary
//        } else {
//            ir.cafebazaar.bazaarpay.R.color.bazaarpay_app_brand_primary
//        }
//        binding.paymentResult.setTextColor(
//            ContextCompat.getColor(this, colorRes)
//        )
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
            startDirectPayContract(contractToken)
        }
    }

    private fun startDirectPayContract(contractToken: String) {
        val options = DirectPayContractOptions(contractToken = contractToken)
        bazaarPayDirectPayContractLauncher.launch(options)
    }
}
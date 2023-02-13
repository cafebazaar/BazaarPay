package ir.cafebazaar.bazaarpaysample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import ir.cafebazaar.bazaarpay.BazaarPayLauncher
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpaysample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val bazaarPayLauncher = BazaarPayLauncher(
        this,
        onSuccess = {
            binding.result.text = "OK!"
            binding.result.setTextColor(ContextCompat.getColor(this, R.color.bazaarpay_app_brand_primary))
        },
        onFailure = { message, code, errorJson ->
            binding.result.text = "Failure! - message: $message , code: $code, errorJson: $errorJson"
            binding.result.setTextColor(ContextCompat.getColor(this, R.color.bazaarpay_error_primary))
        },
        onCancel = {
            binding.result.text = "CANCEL!"
            binding.result.setTextColor(ContextCompat.getColor(this, R.color.bazaarpay_error_primary))
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.payButton.setSafeOnClickListener {
            bazaarPayLauncher.launchBazaarPay(
                checkoutToken = binding.checkoutTokenInput.text.toString(),
                phoneNumber = binding.phoneNumberInput.text.toString(),
                isDarkMode = binding.darkMode.isChecked,
                isEnglish = binding.english.isChecked
            )
        }
    }
}
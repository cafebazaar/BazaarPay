package ir.cafebazaar.bazaarpaysample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import ir.cafebazaar.bazaarpay.BazaarPayLauncher
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.commit
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpaysample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var checkoutToken: String
    private val bazaarPayLauncher = BazaarPayLauncher(
        this,
        {
            binding.result.text = "OK!"
            binding.result.setTextColor(ContextCompat.getColor(this, R.color.bazaarpay_app_brand_primary))

            // If you don't commit the payment in server side you must do it like this in the client
            if (binding.commit.isChecked) {
                commitExample()
            }
        },
        {
            binding.result.text = "CANCEL!"
            binding.result.setTextColor(ContextCompat.getColor(this, R.color.bazaarpay_error_primary))
        }
    )

    private fun commitExample() {
        lifecycleScope.launch {
            commit(
                checkoutToken,
                this@MainActivity,
                onSuccess = {
                    // Successfully committed!
                },
                onFailure = {
                    // You can use ErrorModel (it) to handle the error
                }
            )
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.payButton.setSafeOnClickListener {
            checkoutToken = binding.checkoutTokenInput.text.toString()
            checkoutToken.let {
                bazaarPayLauncher.launchBazaarPay(
                    checkoutToken = it,
                    phoneNumber = binding.phoneNumberInput.text.toString(),
                    isDarkMode = binding.darkMode.isChecked,
                    isEnglish = binding.english.isChecked
                )
            }
        }
    }
}
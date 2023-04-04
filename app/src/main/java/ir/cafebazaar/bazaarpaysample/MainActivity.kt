package ir.cafebazaar.bazaarpaysample

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import ir.cafebazaar.bazaarpay.StartBazaarPay
import ir.cafebazaar.bazaarpay.BazaarPayOptions
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.commit
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpay.trace
import ir.cafebazaar.bazaarpaysample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var checkoutToken: String
    private val bazaarPayLauncher = registerForActivityResult(
        StartBazaarPay()
    ) { isSuccessful ->
        if (isSuccessful) {
            binding.result.text = "OK!"
            binding.result.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.bazaarpay_app_brand_primary
                )
            )
            if (binding.commit.isChecked) {
                commitExample()
            }
        } else {
            binding.result.text = "CANCEL!"
            binding.result.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.bazaarpay_error_primary
                )
            )
        }
    }

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

    private fun traceExample(checkoutToken: String) {
        lifecycleScope.launch {
            trace(
                checkoutToken,
                this@MainActivity,
                onSuccess = {
                    binding.traceResult.text = it.toString()
                },
                onFailure = {
                    binding.traceResult.text = it.message
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
            val options = BazaarPayOptions(
                checkoutToken = binding.checkoutTokenInput.text.toString(),
                phoneNumber = binding.phoneNumberInput.text.toString(),
                isInDarkMode = binding.darkMode.isChecked
            )
            bazaarPayLauncher.launch(options)
        }

        binding.fragmentButton.setSafeOnClickListener {
            val intent = Intent(this, MainFragmentActivity::class.java)
            startActivity(intent)
        }

        binding.traceButton.setSafeOnClickListener {
            checkoutToken = binding.checkoutTokenInput.text.toString()
            traceExample(checkoutToken)
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

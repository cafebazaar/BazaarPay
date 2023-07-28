package ir.cafebazaar.bazaarpay.sample.balance

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import ir.cafebazaar.bazaarpay.getBazaarPayBalance
import ir.cafebazaar.bazaarpay.login.BazaarPayLoginOptions
import ir.cafebazaar.bazaarpay.login.StartLogin
import ir.cafebazaar.bazaarpay.sample.R
import ir.cafebazaar.bazaarpay.sample.databinding.ActivityBalanceSampleBinding
import kotlinx.coroutines.launch

class BalanceSampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBalanceSampleBinding

    private val bazaarPayLoginLauncher = registerForActivityResult(
        StartLogin()
    ) { isSuccessful ->
        if (isSuccessful) {
            loadUserBalance()
        } else {
            Toast.makeText(this, getString(R.string.failure_login), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBalanceSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadUserBalance()
        binding.needLoginButton.setOnClickListener { startLogin() }
    }

    private fun loadUserBalance() = lifecycleScope.launch {
        getBazaarPayBalance(
            this@BalanceSampleActivity,
            onSuccess = {
                updateVisibilityBasedOnLoginState(false)
                binding.txtBalance.text = it.humanReadableAmount
            },
            onFailure = {
                updateVisibilityBasedOnLoginState(false)
                binding.txtBalance.text = it.message
            },
            onLoginNeeded = {
                updateVisibilityBasedOnLoginState(true)
            }
        )
    }

    private fun updateVisibilityBasedOnLoginState(isNeeded: Boolean) {
        binding.balanceGroup.isVisible = isNeeded.not()
        binding.needLoginButton.isVisible = isNeeded
    }

    private fun startLogin(phone: String? = null) {
        val options = BazaarPayLoginOptions(phoneNumber = phone)
        bazaarPayLoginLauncher.launch(options)
    }
}
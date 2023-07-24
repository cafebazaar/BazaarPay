package ir.cafebazaar.bazaarpay.sample.balance

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.cafebazaar.bazaarpay.login.BazaarPayLoginOptions
import ir.cafebazaar.bazaarpay.login.StartLogin
import ir.cafebazaar.bazaarpay.sample.databinding.ActivityBalanceSampleBinding

class BalanceSampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBalanceSampleBinding

    private val bazaarPayLoginLauncher = registerForActivityResult(
        StartLogin()
    ) { isSuccessful ->
        if (isSuccessful) {
            Toast.makeText(this, "لاگین موفقیت آمیز بود", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "لاگین ناموفق بود", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBalanceSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startLogin()
    }

    private fun startLogin(phone: String? = null) {
        val options = BazaarPayLoginOptions(phoneNumber = phone)
        bazaarPayLoginLauncher.launch(options)
    }
}
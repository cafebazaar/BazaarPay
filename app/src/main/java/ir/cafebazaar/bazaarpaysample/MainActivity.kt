package ir.cafebazaar.bazaarpaysample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import ir.cafebazaar.bazaarpay.BazaarPayLauncher
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpaysample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val bazaarPayLauncher = BazaarPayLauncher(
        this,
        {
            binding.result.text = "OK!"
            binding.result.setTextColor(ContextCompat.getColor(this, R.color.green))
        },
        {
            binding.result.text = "CANCEL!"
            binding.result.setTextColor(ContextCompat.getColor(this, R.color.red_notif))
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.payButton.setOnClickListener {
            bazaarPayLauncher.launchBazaarPay(
                checkoutToken = binding.checkoutTokenInput.text.toString(),
                phoneNumber = "09121234567",
                isDarkMode = binding.darkMode.isChecked,
                isEnglish = binding.english.isChecked
            )
        }
    }
}
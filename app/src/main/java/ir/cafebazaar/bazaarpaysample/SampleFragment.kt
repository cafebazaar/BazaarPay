package ir.cafebazaar.bazaarpaysample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.cafebazaar.bazaarpay.BazaarPayLauncher
import ir.cafebazaar.bazaarpay.extensions.setSafeOnClickListener
import ir.cafebazaar.bazaarpaysample.databinding.FragmentSampleBinding

class SampleFragment : Fragment() {
    private lateinit var binding: FragmentSampleBinding

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        BazaarPayLauncher.onResultLauncher(
            result,
            {
                binding.result.text = getString(R.string.message_successful_payment)
                binding.result.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        ir.cafebazaar.bazaarpay.R.color.bazaarpay_app_brand_primary
                    )
                )
            },
            {
                binding.result.text = getString(R.string.message_payment_cancelled)
                binding.result.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        ir.cafebazaar.bazaarpay.R.color.bazaarpay_error_primary
                    )
                )
            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSampleBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.payButton.setSafeOnClickListener {
            BazaarPayLauncher.launchBazaarPay(
                context = requireContext(),
                checkoutToken = binding.checkoutTokenInput.text.toString(),
                phoneNumber = binding.phoneNumberInput.text.toString(),
                isDarkMode = binding.darkMode.isChecked,
                isEnglish = binding.english.isChecked,
                activityResultLauncher = startForResult
            )
        }
    }
}

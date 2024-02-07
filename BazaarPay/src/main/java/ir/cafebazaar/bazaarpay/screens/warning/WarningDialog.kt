package ir.cafebazaar.bazaarpay.screens.warning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.cafebazaar.bazaarpay.data.payment.models.warning.Warning
import ir.cafebazaar.bazaarpay.databinding.BottomSheetWarningBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarPayImageLoader

internal class WarningDialog(
    private val warning: Warning,
    private val onContinue: () -> Unit
) : DialogFragment() {

    private var binding: BottomSheetWarningBinding? = null
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.bindWithRTLSupport(BottomSheetWarningBinding::inflate)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.txtBody?.text = warning.text
        binding?.continueButton?.text = warning.actionText
        warning.icon.getImageUriFromThemedIcon(requireContext()).let { image ->
            BazaarPayImageLoader.loadImage(
                imageView = requireNotNull(binding?.imgLogo),
                imageURI = image
            )
        }
        binding?.continueButton?.setOnClickListener {
            dismiss()
            onContinue()
        }
    }
}
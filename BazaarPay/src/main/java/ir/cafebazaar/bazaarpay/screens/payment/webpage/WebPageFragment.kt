package ir.cafebazaar.bazaarpay.screens.payment.webpage

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import ir.cafebazaar.bazaarpay.analytics.Analytics
import ir.cafebazaar.bazaarpay.base.BaseFragment
import ir.cafebazaar.bazaarpay.databinding.FragmentWebPageBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport

internal class WebPageFragment : BaseFragment(SCREEN_NAME) {

    private val args: WebPageFragmentArgs by lazy(LazyThreadSafetyMode.NONE) {
        WebPageFragmentArgs.fromBundle(requireArguments())
    }

    private var _binding: FragmentWebPageBinding? = null
    private val binding: FragmentWebPageBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflater.bindWithRTLSupport(FragmentWebPageBinding::inflate, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.urlBar.text = args.url
        binding.webView.apply {
            animationListener = { fraction ->
                binding.urlBar.fraction = fraction
            }
            webViewClient = BazaarPayWebViewClient { url ->
                binding.urlBar.text = url
            }
            settings.javaScriptEnabled = true
            loadUrl(args.url)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class BazaarPayWebViewClient(
        private val onUrlChanged: (String) -> Unit,
    ) : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            view?.url?.let { webViewUrl ->
                onUrlChanged.invoke(webViewUrl)
            }
            url?.let {
                Analytics.sendLoadEvent(where = "URL:$it")
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            url?.let {
                Analytics.sendCloseEvent(where = "URL:$it")
            }
        }
    }

    private companion object {

        const val SCREEN_NAME = "WebPage"
    }
}
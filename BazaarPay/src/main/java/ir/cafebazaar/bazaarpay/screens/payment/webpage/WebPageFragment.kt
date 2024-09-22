package ir.cafebazaar.bazaarpay.screens.payment.webpage

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
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
            webViewClient = BazaarPayWebViewClient(
                onUrlChanged = { binding.urlBar.text = it },
                onCloseWebPage = { findNavController().popBackStack() },
            )
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
        private var onCloseWebPage: () -> Unit,
    ) : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            view?.url?.let { webViewUrl ->
                onUrlChanged.invoke(webViewUrl)
            }
            url?.let {
                Analytics.sendLoadEvent(
                    where = "URL:$it",
                    extra = hashMapOf("page_finished" to false),
                )
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            url?.let {
                Analytics.sendLoadEvent(
                    where = "URL:$it",
                    extra = hashMapOf("page_finished" to true),
                )
            }
        }

        @Deprecated(
            "Deprecated in Api 21", ReplaceWith(
                "super.onReceivedError(view, errorCode, description, failingUrl)",
                "android.webkit.WebViewClient"
            )
        )
        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?,
        ) {
            failingUrl?.let {
                sendErrorLog(
                    failingUrl = failingUrl,
                    errorCode = errorCode,
                    errorType = ERROR_TYPE_RESOURCE,
                    description = description,
                )
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {
            if (request.isForMainFrame &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            ) {
                sendErrorLog(
                    failingUrl = request.url.toString(),
                    errorCode = error.errorCode,
                    errorType = ERROR_TYPE_RESOURCE,
                    description = error.description.toString(),
                )
            } else {
                super.onReceivedError(view, request, error)
            }
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler,
            error: SslError,
        ) {
            sendErrorLog(
                failingUrl = error.url,
                errorCode = error.primaryError,
                errorType = ERROR_TYPE_SSL,
                description = error.certificate.toString(),
            )
            super.onReceivedSslError(view, handler, error)
        }

        @Deprecated("Deprecated in Api 24")
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String,
        ): Boolean {
            return overrideUrl(view.context, url)
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest,
        ): Boolean {
            return overrideUrl(view.context, request.url.toString())
        }

        private fun sendErrorLog(
            failingUrl: String,
            errorCode: Int,
            errorType: String,
            description: String?,
        ) {
            Analytics.sendErrorEvent(
                where = "URL:$failingUrl",
                extra = hashMapOf(
                    "code" to errorCode,
                    "type" to errorType,
                    "description" to description.orEmpty(),
                ),
            )
        }

        private fun overrideUrl(context: Context, url: String): Boolean {
            with(url.lowercase()) {
                if (startsWith("http://") ||
                    startsWith("https://")
                ) {
                    return false
                }
            }

            return try {
                if (url.startsWith("bazaar://")) {
                    onCloseWebPage()
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    intent.`package` = context.packageName
                    context.startActivity(intent)
                    true
                } else {
                    false
                }
            } catch (ignored: Exception) {
                false
            }
        }
    }

    private companion object {

        const val SCREEN_NAME = "WebPage"

        const val ERROR_TYPE_RESOURCE = "resource"
        const val ERROR_TYPE_SSL = "ssl"
    }
}
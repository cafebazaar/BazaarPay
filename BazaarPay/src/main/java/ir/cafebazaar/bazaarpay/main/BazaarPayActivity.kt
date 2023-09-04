package ir.cafebazaar.bazaarpay.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import ir.cafebazaar.bazaarpay.FinishCallbacks
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.analytics.viewmodel.AnalyticsViewModel
import ir.cafebazaar.bazaarpay.arg.BazaarPayActivityArgs
import ir.cafebazaar.bazaarpay.databinding.ActivityBazaarPayBinding
import ir.cafebazaar.bazaarpay.utils.bindWithRTLSupport
import java.util.Locale

class BazaarPayActivity : AppCompatActivity(), FinishCallbacks {

    private lateinit var binding: ActivityBazaarPayBinding
    private var args: BazaarPayActivityArgs? = null
    private var currentUiMode: Number? = null

    private val analyticsViewModel: AnalyticsViewModel by viewModels()
    private val mainViewModel: BazaarPayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        initNightMode()
        initServiceLocator(savedInstanceState)
        super.onCreate(savedInstanceState)
        binding = layoutInflater.bindWithRTLSupport(ActivityBazaarPayBinding::inflate)
        setContentView(binding.root)

        args = intent.getParcelableExtra(BAZAARPAY_ACTIVITY_ARGS)

        handleIntent(intent)

        startFadeInAnimation()

        analyticsViewModel.listenThreshold()

        registerObservers()
    }

    private fun registerObservers() {
        mainViewModel.paymentSuccessLiveData.observe(this) {
            navigateToThankYouPageIfIsNotShowingNow()
        }
    }

    override fun onStart() {
        super.onStart()
        // disable animation because background and the bottom sheet have different animations.
        overridePendingTransition(0, 0)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, android.R.anim.fade_out)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(setLocale(newBase))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BAZAARPAY_ACTIVITY_ARGS, args)
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        validateArguments(args) {
            finishActivity()
            return
        }
        when {
            isIncreaseBalanceDoneIntent(intent) -> {
                navigateToThankYouPageIfIsNotShowingNow()
            }

            isDirectDebitActivationIntent(intent) -> {
                findNavController(R.id.nav_host_fragment_bazaar_pay).navigate(
                    resId = R.id.open_payment_methods,
                    args = null,
                    navOptions = navOptions {
                        popUpTo(R.id.paymentMethodsFragment)
                    }
                )
            }
        }
    }

    private fun navigateToThankYouPageIfIsNotShowingNow() {
        val currentFragment = findNavController(R.id.nav_host_fragment_bazaar_pay).currentDestination?.id
        if (currentFragment != R.id.paymentThankYouPageFragment) {
            findNavController(R.id.nav_host_fragment_bazaar_pay).navigate(
                R.id.open_paymentThankYouPageFragment
            )
        }
    }

    private inline fun validateArguments(
        args: BazaarPayActivityArgs?,
        onInvalidInputs: () -> Unit
    ) {
        if (args == null) {
            onInvalidInputs()
            return
        }
        when (args) {
            is BazaarPayActivityArgs.Normal -> {
                if (ServiceLocator.getOrNull<String>(
                        ServiceLocator.CHECKOUT_TOKEN
                    ).isNullOrEmpty()
                ) {
                    onInvalidInputs()
                }
            }

            is BazaarPayActivityArgs.DirectPayContract -> {
                if (ServiceLocator.getOrNull<String>(
                        ServiceLocator.DIRECT_PAY_CONTRACT_TOKEN
                    ).isNullOrEmpty()
                ) {
                    onInvalidInputs()
                }
            }

        }
    }

    private fun startFadeInAnimation() {
        val loadAnimation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in)
        binding.bazaarPayBackground.startAnimation(loadAnimation)
    }

    private fun setLocale(
        context: Context,
        uiMode: Int = Configuration.UI_MODE_NIGHT_UNDEFINED
    ): Context {
        return updateResources(context, uiMode)
    }

    private fun updateResources(
        context: Context,
        uiMode: Int
    ): Context {
        var contextWithCorrectTheme = setCorrectUiMode(context, uiMode)
        val newLocale = Locale("fa")
        Locale.setDefault(newLocale)

        val res = contextWithCorrectTheme.resources
        val config = Configuration(res.configuration)
        contextWithCorrectTheme = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                config.setLocale(newLocale)

                val localeList = LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)

                contextWithCorrectTheme.createConfigurationContext(config)
            }

            else -> {
                config.setLocale(newLocale)
                contextWithCorrectTheme.createConfigurationContext(config)
            }
        }
        return contextWithCorrectTheme
    }

    private fun setCorrectUiMode(context: Context, uiMode: Int): Context {
        val contextResource = context.resources

        currentUiMode = when {
            uiMode == Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                contextResource.configuration.uiMode
            }

            isDarkMode() == true -> {
                Configuration.UI_MODE_NIGHT_YES
            }

            isDarkMode() == null -> {
                Configuration.UI_MODE_NIGHT_UNDEFINED
            }

            else -> {
                Configuration.UI_MODE_NIGHT_NO
            }
        }

        val overrideConfiguration = Configuration(contextResource.configuration).apply {
            this.uiMode = currentUiMode?.toInt() ?: Configuration.UI_MODE_NIGHT_NO
        }
        return context.createConfigurationContext(overrideConfiguration)
    }

    private fun isDarkMode(): Boolean? {
        return ServiceLocator.getOrNull(ServiceLocator.IS_DARK)
    }

    private fun initNightMode() {
        val isDarkEnable = isDarkMode()
        isDarkEnable ?: return
        if (isDarkEnable) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }.also { modeToSet ->
            AppCompatDelegate.setDefaultNightMode(modeToSet)
        }
    }

    private fun initServiceLocator(savedInstanceState: Bundle?) {
        val restoredArgs = savedInstanceState?.get(BAZAARPAY_ACTIVITY_ARGS) as? BazaarPayActivityArgs
        when (restoredArgs) {
            is BazaarPayActivityArgs.Normal -> {
                with(restoredArgs) {
                    ServiceLocator.initializeConfigsForNormal(
                        checkoutToken = checkoutToken,
                        phoneNumber = phoneNumber,
                        isAutoLoginEnable = isAutoLoginEnable,
                        autoLoginPhoneNumber = autoLoginPhoneNumber
                    )
                }
            }

            is BazaarPayActivityArgs.DirectPayContract -> {
                with(restoredArgs) {
                    ServiceLocator.initializeConfigsForDirectPayContract(
                        contractToken = contractToken,
                        phoneNumber = phoneNumber,
                        message = message
                    )
                }
            }
        }

        ServiceLocator.initializeDependencies(context = applicationContext)
    }

    override fun onSuccess() {
        setResult(RESULT_OK)
        finishActivity()
    }

    override fun onCanceled() {
        setResult(RESULT_CANCELED)
        finishActivity()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.onActivityResumed()
    }

    private fun finishActivity() {
        analyticsViewModel.onFinish()
        finish()
    }

    private fun isIncreaseBalanceDoneIntent(intent: Intent?): Boolean {
        return intent?.dataString?.lowercase()?.contains("increase_balance")?.and(
            intent.dataString?.lowercase()?.contains("done") == true
        ) == true
    }

    private fun isDirectDebitActivationIntent(intent: Intent?): Boolean {
        return intent?.dataString?.lowercase()?.contains("direct_debit_activation")?.and(
            (intent.dataString?.lowercase()?.contains("active") == true).or(
                intent.dataString?.lowercase()?.contains("in_progress") == true
            )
        ) == true
    }

    companion object {

        const val BAZAARPAY_ACTIVITY_ARGS = "bazaarpayActivityArgs"
    }
}
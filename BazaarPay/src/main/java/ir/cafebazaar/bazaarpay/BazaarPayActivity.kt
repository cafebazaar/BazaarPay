package ir.cafebazaar.bazaarpay

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import ir.cafebazaar.bazaarpay.databinding.ActivityBazaarPayBinding
import java.util.*

class BazaarPayActivity : AppCompatActivity(), FinishCallbacks {

    private lateinit var binding: ActivityBazaarPayBinding
    private lateinit var currentUiMode: Number

    override fun onCreate(savedInstanceState: Bundle?) {
        initNightMode()
        super.onCreate(savedInstanceState)
        binding = ActivityBazaarPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntent(intent)

        initServiceLocator()
        startFadeInAnimation()
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
        if(ServiceLocator.isConfigInitiated()){
            setLocale(newBase)
        } else {
            newBase
        }.also {
            super.attachBaseContext(it)
        }
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
        if (ServiceLocator.getOrNull<String>(ServiceLocator.CHECKOUT_TOKEN).isNullOrEmpty()) {
            finishActivity()
        }
        when {
            intent?.dataString?.lowercase()?.contains("increase_balance")?.and(
                intent.dataString?.lowercase()?.contains("done") == true
            ) == true -> {
                findNavController(R.id.nav_host_fragment_bazaar_pay).navigate(
                    R.id.open_paymentThankYouPageFragment
                )
            }
            intent?.dataString?.lowercase()?.contains("direct_debit_activation")?.and(
                (intent.dataString?.lowercase()?.contains("active") == true).or(
                    intent.dataString?.lowercase()?.contains("in_progress") == true
                )
            ) == true -> {
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
        val newLocale = Locale(ServiceLocator.get(ServiceLocator.LANGUAGE))
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
            isDarkMode() -> {
                Configuration.UI_MODE_NIGHT_YES
            }
            else -> {
                Configuration.UI_MODE_NIGHT_NO
            }
        }

        val overrideConfiguration = Configuration(contextResource.configuration).apply {
            this.uiMode = currentUiMode.toInt()
        }
        return context.createConfigurationContext(overrideConfiguration)
    }

    private fun isDarkMode(): Boolean {
        return ServiceLocator.getOrNull<Boolean>(ServiceLocator.IS_DARK)?:false
    }

    private fun initNightMode() {
        if (isDarkMode()) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }.also { modeToSet ->
            AppCompatDelegate.setDefaultNightMode(modeToSet)
        }
    }

    private fun initServiceLocator() {
        if(ServiceLocator.isConfigInitiated()) {
            ServiceLocator.initializeDependencies(
                activity = this
            )
        }
    }

    override fun onSuccess() {
        setResult(RESULT_OK)
        finishActivity()
    }

    override fun onCanceled() {
        setResult(RESULT_CANCELED)
        finishActivity()
    }

    private fun finishActivity() {
        ServiceLocator.clear()
        finish()
    }
}
package ir.cafebazaar.bazaarpay.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class SmsPermissionReceiver : BroadcastReceiver() {

    private val accountRepository: AccountRepository = ServiceLocator.get()
    private val globalDispatchers: GlobalDispatchers = ServiceLocator.get()

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)?.also { consentIntent ->
                        CoroutineScope(globalDispatchers.iO).launch {
                            accountRepository.setSmsPermissionObservable(consentIntent)
                        }
                    }
                }
                CommonStatusCodes.TIMEOUT -> {

                }
            }
        }
    }
}
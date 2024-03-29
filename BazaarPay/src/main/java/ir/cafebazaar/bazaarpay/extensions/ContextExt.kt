package ir.cafebazaar.bazaarpay.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.data.bazaar.models.InvalidPhoneNumberException

fun Context.getConnectivityManager() =
    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.isNetworkAvailable(): Boolean {
    val cm = getConnectivityManager()
    return cm.activeNetworkInfo?.isConnected ?: false
}

fun Context.getReadableErrorMessage(errorModel: ErrorModel?, longText: Boolean = true): String = when (errorModel) {
    is ErrorModel.NetworkConnection ->
        if (longText) {
            if (!isNetworkAvailable()) getString(R.string.bazaarpay_no_internet_connection)
            else getString(R.string.bazaarpay_error_server_connection_failure)
        } else {
            if (!isNetworkAvailable()) getString(R.string.bazaarpay_no_internet_connection_short)
            else getString(R.string.bazaarpay_error_server_connection_failure_short)
        }
    is ErrorModel.NotFound -> {
        if (errorModel.message.isEmpty()) {
            getString(R.string.bazaarpay_data_not_found)
        } else {
            errorModel.message
        }
    }
    is ErrorModel.RateLimitExceeded -> getString(R.string.bazaarpay_rate_limit_exceeded)
    is InvalidPhoneNumberException -> getString(R.string.bazaarpay_wrong_phone_number)
    is ErrorModel.Server, ErrorModel.UnExpected -> getString(R.string.bazaarpay_error_server_connection_failure)
    else -> {
        if (errorModel == null || errorModel.message.isEmpty()) {
            getString(R.string.bazaarpay_error_server_connection_failure)
        } else {
            errorModel.message
        }
    }
}

fun Context.googlePlayServicesAvailabilityStatusCode(): Int {
    return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
}

fun Context.isGooglePlayServicesAvailable(): Boolean {
    return googlePlayServicesAvailabilityStatusCode() == ConnectionResult.SUCCESS
}

fun Context.isRTL(): Boolean {
    return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

fun Context.openUrl(
    url: String
) {
    if (url.isEmpty()) {
        return
    }

    if (url.startsWith("bazaar://")) {
        Throwable("trying to open a $url link with chrome")
    }

    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(url)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        this.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        IllegalStateException("No Activity Found for opening :${intent.data}", e)
    }
}
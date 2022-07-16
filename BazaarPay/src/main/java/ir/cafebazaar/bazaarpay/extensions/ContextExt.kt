package ir.cafebazaar.bazaarpay.extensions

import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.app.ActivityManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.models.*
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarGlideModule
import java.util.*

fun Context.getConnectivityManager() =
    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.layoutInflater: LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

val Context.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.isNetworkAvailable(): Boolean {
    val cm = getConnectivityManager()
    return cm.activeNetworkInfo?.isConnected ?: false
}

fun Context.getReadableErrorMessage(errorModel: ErrorModel?, longText: Boolean = true): String = when (errorModel) {
    is ErrorModel.NetworkConnection ->
        if (longText) {
            if (!isNetworkAvailable()) getString(R.string.no_internet_connection)
            else getString(R.string.error_server_connection_failure)
        } else {
            if (!isNetworkAvailable()) getString(R.string.no_internet_connection_short)
            else getString(R.string.error_server_connection_failure_short)
        }
    is ErrorModel.NotFound -> {
        if (errorModel.message.isEmpty()) {
            getString(R.string.data_not_found)
        } else {
            errorModel.message
        }
    }
    is ErrorModel.RateLimitExceeded -> getString(R.string.rate_limit_exceeded)
    is InvalidPhoneNumberException -> getString(R.string.wrong_phone_number)
    is ErrorModel.Server, ErrorModel.UnExpected -> getString(R.string.error_server_connection_failure)
    else -> {
        if (errorModel == null || errorModel.message.isEmpty()) {
            getString(R.string.error_server_connection_failure)
        } else {
            errorModel.message
        }
    }
}

fun Context.secondsToStringTime(second: Long): String {

    val stringBuilder = StringBuilder()
    val formatter = Formatter(stringBuilder, Locale(ServiceLocator.get(ServiceLocator.LANGUAGE)))

    val seconds = second % 60
    val minutes = second / 60 % 60
    val hours = second / 3600

    stringBuilder.setLength(0)
    return if (hours > 0) {
        formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
        formatter.format("%02d:%02d", minutes, seconds).toString()
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

fun Context.getErrorIcon(errorModel: ErrorModel?): Int = when (errorModel) {
    is ErrorModel.NetworkConnection ->
        if (isNetworkAvailable().not()) R.drawable.ic_signal_wifi_off_icon_primary_24dp_old
        else R.drawable.ic_error_outline_icon_primary_24dp_old
    else -> R.drawable.ic_error_outline_icon_primary_24dp_old
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

@ColorInt
fun Context.getBalanceTextColor(balance: Long): Int {
    return if (balance < 0) {
        ContextCompat.getColor(this, R.color.error_primary)
    } else {
        ContextCompat.getColor(this, R.color.text_secondary)
    }
}

fun Context.isHighPerformingDevice(): Boolean {
    val activityManager = (this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
    return !ActivityManagerCompat.isLowRamDevice(activityManager) &&
            Runtime.getRuntime().availableProcessors() >= BazaarGlideModule.AVAILABLE_PROCESSORS &&
            activityManager.memoryClass >= BazaarGlideModule.AVAILABLE_MEMORY
}
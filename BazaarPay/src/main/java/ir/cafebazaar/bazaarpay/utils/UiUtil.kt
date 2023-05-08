package ir.cafebazaar.bazaarpay.utils

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ir.cafebazaar.bazaarpay.R

@ColorInt
internal fun getBalanceTextColor(context: Context, balance: Long): Int {
    return if (balance < 0) {
        ContextCompat.getColor(context, R.color.bazaarpay_error_primary)
    } else {
        ContextCompat.getColor(context, R.color.bazaarpay_text_primary)
    }
}

internal fun isDarkMode(context: Context): Boolean {
    val darkModeFlag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}
package ir.cafebazaar.bazaarpay.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ir.cafebazaar.bazaarpay.R

@ColorInt
fun getBalanceTextColor(context: Context, balance: Long): Int {
    return if (balance < 0) {
        ContextCompat.getColor(context, R.color.bazaarpay_error_primary)
    } else {
        ContextCompat.getColor(context, R.color.bazaarpay_text_secondary)
    }
}
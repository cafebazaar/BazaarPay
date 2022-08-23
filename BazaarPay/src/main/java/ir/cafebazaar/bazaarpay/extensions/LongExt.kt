package ir.cafebazaar.bazaarpay.extensions

import android.content.Context
import ir.cafebazaar.bazaarpay.R
import java.util.*


private const val TOMAN_RIAL_DIFFERENCE = 10

fun Long.toToman(): Long = this / TOMAN_RIAL_DIFFERENCE

private const val MAXIMUM_VALUE_DOES_NOT_NEED_SEPARATOR = 999

fun Long.toPriceFormat(context: Context, locale: Locale): String {

    val stringRes = when {
        this > MAXIMUM_VALUE_DOES_NOT_NEED_SEPARATOR -> {
            R.string.price_placeholder_seperator
        }
        else -> {
            R.string.price_placeholder
        }
    }

    val priceWithSeparator = context.getString(stringRes, this)
    return priceWithSeparator.persianDigitsIfPersian(locale)
}
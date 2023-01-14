package ir.cafebazaar.bazaarpay.extensions

import java.util.Locale

private const val TOMAN_RIAL_DIFFERENCE = 10

fun Long.toToman(): Long = this / TOMAN_RIAL_DIFFERENCE

private const val MAXIMUM_VALUE_DOES_NOT_NEED_SEPARATOR = 999

fun Long.toPriceFormat(locale: Locale): String {
    val priceString = String.format("%,d", this)
    val priceWithSeparator = "$priceString " + when (locale.language) {
        "fa" -> {
            "تومان"
        }
        else -> {
            if (this > MAXIMUM_VALUE_DOES_NOT_NEED_SEPARATOR) {
                 "Tomans"
            } else {
                "Toman"
            }
        }
    }
    return priceWithSeparator.persianDigitsIfPersian(locale)
}
package ir.cafebazaar.bazaarpay.utils

import ir.cafebazaar.bazaarpay.ServiceLocator
import java.util.*

fun secondsToStringTime(second: Long): String {

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
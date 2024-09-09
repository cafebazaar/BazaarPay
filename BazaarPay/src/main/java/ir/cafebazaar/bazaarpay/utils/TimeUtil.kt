package ir.cafebazaar.bazaarpay.utils

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

typealias Millisecond = Long
typealias Second = Long
typealias Minute = Long
typealias Hour = Long

private const val ONE_MINUTE_IN_SECONDS = 60
private const val TEN_SECONDS = 10

fun Second.secondsToStringTime(): String {
    return toDuration(DurationUnit.SECONDS).toStringTime()
}

fun Long.toStringTime(): String {
    return toDuration(DurationUnit.MILLISECONDS).toStringTime()
}

fun Duration.toStringTime(): String {
    return toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            "%d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }
}

fun Long.toFormattedVideoDuration(): String? {
    if (this == 0L) {
        return null
    }
    return StringBuilder()
        .append(this / ONE_MINUTE_IN_SECONDS)
        .append(":")
        .also {
            val second = this % ONE_MINUTE_IN_SECONDS
            if (second < TEN_SECONDS) {
                it.append("0")
            }
            it.append(second)
        }.toString()
}
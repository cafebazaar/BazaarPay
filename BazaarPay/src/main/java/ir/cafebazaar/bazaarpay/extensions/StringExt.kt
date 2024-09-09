package ir.cafebazaar.bazaarpay.extensions

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.ServiceLocator
import java.math.BigDecimal
import java.util.Locale
import java.util.regex.Pattern

fun String.isValidPhoneNumber() = isNotEmpty() && matches(PHONE.toRegex())


// See android.util.Patterns.PHONE
private val PHONE: Pattern =
    Pattern.compile("""(\+[0-9]+[\- .]*)?(\([0-9]+\)[\- .]*)?([0-9][0-9\- .]+[0-9])""")

fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
}

fun String.localizeNumber(context: Context): String =
    try {
        val result = StringBuilder()
        var phone = this
        while (phone.startsWith("0")) {
            result.append(context.getString(R.string.bazaarpay_number_placeholder, 0))
            phone = phone.substring(1)
        }

        result.append(context.getString(R.string.bazaarpay_number_placeholder, this.toLong()))
        result.toString()
    } catch (e: Exception) {
        this
    }

const val NATIONAL_ID_LENGTH = 10

fun String.isValidNationalId(): Boolean {
    return takeIf { length == NATIONAL_ID_LENGTH }
        ?.mapNotNull(Char::digitToIntOrNull)
        ?.takeIf { it.size == NATIONAL_ID_LENGTH }?.let {
            val check = it[9]
            val sum = it.slice(0..8).mapIndexed { i, x -> x * (10 - i) }.sum() % 11
            if (sum < 2) check == sum else check + sum == 11
        } ?: false
}

fun String.persianDigitsIfPersian(locale: Locale): String {
    val lang = locale.language
    val country = locale.country
    return if ("fa" == lang && "TJ" != country) {
        persianDigits().fixAccessibility()
    } else {
        this
    }
}

private const val PERSIAN_ZERO: Char = 0x06f0.toChar()
private const val DIGIT_DIFF = (PERSIAN_ZERO - '0').toChar()
private fun String.persianDigits(): String {
    var result = ""
    var char: Char
    var index = 0

    while (index < length) {
        char = toCharArray()[index]
        when (char) {
            in '0'..'9' -> result += (DIGIT_DIFF.code + char.code).toChar().toString()
            ',' -> result += '٬'
            else -> result += char
        }
        index++
    }
    return result
}

fun String.digits(): Long {
    val numericValue = filter { it.isDigit() }.toEnglishDigit()
    return if (numericValue.isEmpty()) {
        0
    } else {
        numericValue.toLong()
    }
}

fun String.fixAccessibility(
    isAccessibilityEnabled: Boolean = ServiceLocator.isAccessibilityEnable(),
): String {
    return if (isAccessibilityEnabled) {
        replace('٬', ',')
    } else {
        this
    }
}

private fun String.toEnglishDigit(): String {
    return try {
        if (length > 0) {
            BigDecimal(this).toString()
        } else {
            "0"
        }
    } catch (ignored: Exception) {
        "0"
    }
}
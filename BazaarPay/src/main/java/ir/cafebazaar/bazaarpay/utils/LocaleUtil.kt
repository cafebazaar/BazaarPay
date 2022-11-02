package ir.cafebazaar.bazaarpay.utils

fun getLanguage(isEnglish: Boolean): String {
    return if (isEnglish) {
        "en"
    } else {
        "fa"
    }
}

fun getLanguageNumber(isEnglish: Boolean): Int {
    return if (isEnglish) {
        1
    } else {
        2
    }
}
package ir.cafebazaar.bazaarpay.extensions

import android.app.Activity
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard(windowToken: IBinder? = null) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(windowToken ?: view.windowToken, 0)
}
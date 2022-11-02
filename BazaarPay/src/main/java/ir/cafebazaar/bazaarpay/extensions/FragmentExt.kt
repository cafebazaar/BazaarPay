package ir.cafebazaar.bazaarpay.extensions

import android.os.IBinder
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard(windowToken: IBinder? = null) {
    activity?.hideKeyboard(windowToken)
}

fun Fragment.toastMessage(message: String, toastLength: Int = Toast.LENGTH_SHORT) {
    context?.let { Toast.makeText(it, message, toastLength).show() }
}
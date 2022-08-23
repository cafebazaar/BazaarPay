package ir.cafebazaar.bazaarpay.extensions

import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.makeMeasureSpec
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

fun Fragment.hideKeyboard(windowToken: IBinder? = null) {
    activity?.hideKeyboard(windowToken)
}

fun Fragment.toastMessage(message: String, toastLength: Int = Toast.LENGTH_SHORT) {
    context?.let { Toast.makeText(it, message, toastLength).show() }
}
package ir.cafebazaar.bazaarpay.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.navigateSafe(directions: NavDirections) {
    navigateSafe(directions.actionId, directions.arguments)
}

fun NavController.navigateSafe(@IdRes resId: Int, args: Bundle? = null) {
    if (currentDestination?.getAction(resId) != null) {
        navigate(resId, args)
    }
}
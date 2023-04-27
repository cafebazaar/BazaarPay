package ir.cafebazaar.bazaarpay.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.ApplicationInfo.FLAG_SUPPORTS_RTL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

class ContextRTLSupportWrapper(context: Context) : ContextWrapper(context) {

    override fun getApplicationInfo(): ApplicationInfo {
        return ApplicationInfo(super.getApplicationInfo()).apply {
            flags = flags or FLAG_SUPPORTS_RTL
        }
    }
}

fun <T : ViewBinding> ViewGroup.bindWithRTLSupport(
    binder: (LayoutInflater, ViewGroup, Boolean) -> T,
    attachToRoot: Boolean = false
): T {
    return LayoutInflater.from(context)
        .bindWithRTLSupport(
            rootBinder = binder,
            parent = this,
            attachToRoot = attachToRoot
        )
}

fun <T : ViewBinding> LayoutInflater.bindWithRTLSupport(
    binder: (LayoutInflater) -> T,
): T {
    return bindWithRTLSupport(inflaterBinder = binder)
}

fun <T : ViewBinding> LayoutInflater.bindWithRTLSupport(
    binder: (LayoutInflater, ViewGroup, Boolean) -> T,
    container: ViewGroup?,
    attachToRoot: Boolean = false
): T {
    return this.bindWithRTLSupport(
        rootBinder = binder,
        parent = container,
        attachToRoot = attachToRoot
    )
}

private fun <T : ViewBinding> LayoutInflater.bindWithRTLSupport(
    inflaterBinder: ((LayoutInflater) -> T)? = null,
    rootBinder: ((LayoutInflater, ViewGroup, Boolean) -> T)? = null,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): T {
    fun inflate(inflater: LayoutInflater): T {
        return inflaterBinder?.invoke(inflater)
            ?: rootBinder?.invoke(inflater, requireNotNull(parent), attachToRoot)
            ?: error("inflaterBinder and rootBinder could not be null at same time")
    }

    val rtl = (context.applicationInfo.flags and FLAG_SUPPORTS_RTL) == FLAG_SUPPORTS_RTL
    return if (rtl) {
        // android:supportsRtl is already true in manifest
        inflate(this)
    } else {
        // force support rtl for this view and its children
        inflate(
            cloneInContext(
                ContextRTLSupportWrapper(context)
            )
        ).apply {
            // update layout direction based on current locale
            root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }
}
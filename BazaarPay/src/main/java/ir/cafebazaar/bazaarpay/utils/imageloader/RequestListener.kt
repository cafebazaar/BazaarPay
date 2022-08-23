package ir.cafebazaar.bazaarpay.utils.imageloader

import android.view.View

/**
 * Simpler version of [com.bumptech.glide.request.RequestListener]
 * created to not expose glide dependency
 */
internal interface RequestListener<V : View, R> {

    fun onLoadFailed(e: Exception?, model: Any, isFirstResource: Boolean): Boolean {
        return false
    }

    fun onResourceReady(resource: R, model: Any, view: V, isFirstResource: Boolean): Boolean {
        return false
    }
}
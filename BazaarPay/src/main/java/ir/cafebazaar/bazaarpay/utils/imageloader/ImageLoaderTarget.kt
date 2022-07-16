package ir.cafebazaar.bazaarpay.utils.imageloader

import android.graphics.drawable.Drawable

internal interface ImageLoaderTarget {

    fun onResourceReady(drawable: Drawable)
    fun onLoadFailed() {}
    fun onLoadCleared(placeholder: Drawable?) {}
}
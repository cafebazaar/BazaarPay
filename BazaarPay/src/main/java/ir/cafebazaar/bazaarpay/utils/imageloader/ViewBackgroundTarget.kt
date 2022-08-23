package ir.cafebazaar.bazaarpay.utils.imageloader

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition

internal abstract class ViewBackgroundTarget<Z>(view: View) : CustomViewTarget<View, Z>(view) {
    override fun onLoadFailed(errorDrawable: Drawable?) {
        view.background = errorDrawable
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        view.background = placeholder
    }

    override fun onResourceReady(resource: Z, transition: Transition<in Z>?) {
        setResource(resource)
    }

    protected abstract fun setResource(resource: Z)
}

package ir.cafebazaar.bazaarpay.utils.imageloader

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.RequestListener as GlideRequestListener

@Suppress("SwallowedException", "LongParameterList", "TooManyFunctions")
internal object BazaarPayImageLoader {

    // Fixme: Remove roundedCornerCenterCrop and use scaleType on imageViews.
    @SuppressLint("CheckResult")
    fun loadImage(
        imageView: ImageView,
        imageURI: String,
        isCircular: Boolean = false,
        hasAnimation: Boolean = false,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes placeHolderId: Int? = null,
        thumbnailUrl: String? = null,
        roundedCorner: Int = 0,
        roundedCornerCenterCrop: Int = 0,
        listener: RequestListener<ImageView, Drawable>? = null
    ) {
        val requestOption = RequestOptions().apply {
            if (placeHolderDrawable != null) {
                placeholder(placeHolderDrawable)
            } else if (placeHolderId != null) {
                placeholder(placeHolderId)
            }

            setTransformation(
                imageView,
                roundedCorner,
                roundedCornerCenterCrop
            )
        }
        val glideRequest = Glide.with(imageView.context)
            .load(Uri.parse(imageURI))
            .centerInside()
            .apply {
                if (isCircular) {
                    circleCrop()
                }
                listener?.let { addListener(listener) }
            }

        if (hasAnimation) {
            val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            glideRequest.transition(withCrossFade(factory))
        }

        if (isCircular) {
            glideRequest.circleCrop()
        }

        glideRequest.thumbnail(
            Glide.with(imageView.context)
                .load(thumbnailUrl)
        ).apply(requestOption)
            .into(imageView)
    }

    private fun RequestOptions.setTransformation(
        imageView: ImageView,
        roundedCorner: Int,
        roundedCornerCenterCrop: Int
    ): RequestOptions {
        return when {
            roundedCorner > 0 -> {
                transformByScaleType(imageView, roundedCorner)
            }
            roundedCornerCenterCrop > 0 -> {
                transform(CenterCrop(), RoundedCorners(roundedCornerCenterCrop))
            }
            else -> this
        }
    }

    private fun RequestBuilder<Drawable>.addListener(
        listener: RequestListener<ImageView, Drawable>
    ): RequestBuilder<Drawable> {
        return addListener(object : GlideRequestListener<Drawable> {
            override fun onLoadFailed(
                exception: GlideException?,
                model: Any,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                return listener.onLoadFailed(exception, model, isFirstResource)
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return listener.onResourceReady(
                    resource,
                    model,
                    // This cast always successes because view is always ImageView
                    (target as ImageViewTarget<*>).view,
                    isFirstResource
                )
            }
        })
    }

    private fun RequestOptions.transformByScaleType(
        imageView: ImageView,
        roundedCorner: Int,
    ): RequestOptions {
        val roundedCornersTransformation = RoundedCorners(roundedCorner)
        return when (imageView.scaleType) {
            ImageView.ScaleType.FIT_XY -> {
                transform(CenterInside(), roundedCornersTransformation)
            }
            ImageView.ScaleType.CENTER_CROP -> {
                transform(CenterCrop(), roundedCornersTransformation)
            }
            ImageView.ScaleType.FIT_CENTER -> {
                transform(FitCenter(), roundedCornersTransformation)
            }
            else -> {
                transform(roundedCornersTransformation)
            }
        }
    }
}
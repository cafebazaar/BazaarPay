package ir.cafebazaar.bazaarpay.utils.imageloader

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.target.Target
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import com.bumptech.glide.request.RequestListener as GlideRequestListener

@Suppress("SwallowedException", "LongParameterList", "TooManyFunctions")
internal object ImageLoader {

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
        val glideRequest = GlideApp.with(imageView.context)
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
            GlideApp.with(imageView.context)
                .load(thumbnailUrl)
        ).apply(requestOption)
            .into(imageView)
    }

    fun preloadImage(
        imageURI: String,
        context: Context
    ) {

        GlideApp.with(context)
            .downloadOnly()
            .load(Uri.parse(imageURI))
            .submit()
    }

    /**
     * Get Your Image Bitmap synchronously,
     * Caution: This method is blocking, and should not call on @main Thread.
     */
    @WorkerThread
    fun getImageDrawable(glideRequest: GlideRequests, file: File): Drawable? {
        val futureDrawable: FutureTarget<Drawable?> = glideRequest
            .asDrawable()
            .load(file)
            .submit()
        return futureDrawable.get()
    }

    /**
     * Get Your Image Bitmap synchronously,
     * Caution: This method is blocking, and should not call on @main Thread.
     */
    @WorkerThread
    fun getImageFile(glideRequest: GlideRequests, url: String): File? {
        val futureDrawable: FutureTarget<File?> = glideRequest
            .asFile()
            .load(url)
            .submit()
        return futureDrawable.get()
    }

    @SuppressLint("CheckResult")
    fun loadImage(
        context: Context,
        imageURI: String,
        onlyLoadFromCache: Boolean = false,
        isCircular: Boolean = false,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes placeHolderId: Int? = null,
        roundedCorner: Int = 0,
        size: Int = 0,
        target: ImageLoaderTarget
    ) {

        val requestOption = RequestOptions().apply {
            if (placeHolderDrawable != null) {
                placeholder(placeHolderDrawable)
            } else if (placeHolderId != null) {
                placeholder(placeHolderId)
            }
            if (roundedCorner > 0) {
                transform(RoundedCorners(roundedCorner))
            }
            onlyRetrieveFromCache(onlyLoadFromCache)
            if (size > 0) {
                override(size)
            }
        }

        val glideRequest = GlideApp.with(context)
            .asDrawable()
            .load(Uri.parse(imageURI))
            .centerInside()

        if (isCircular) {
            glideRequest.apply(circleCropTransform())
        }
        glideRequest.apply(requestOption).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                target.onResourceReady(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                target.onLoadCleared(placeholder)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                target.onLoadFailed()
            }
        })
    }

    fun getGlideRequest(context: Context) = GlideApp.with(context)

    fun isExistInCache(glideRequest: GlideRequests, items: List<String>): Boolean {
        var isCached = true
        val requestOption = RequestOptions().onlyRetrieveFromCache(true)
        try {
            for (item in items) {
                glideRequest.setDefaultRequestOptions(requestOption)
                    .load(Uri.parse(item)).submit().get()
                    ?: return false
            }
        } catch (e: CancellationException) {
            isCached = false
        } catch (e: ExecutionException) {
            isCached = false
        } catch (e: InterruptedException) {
            isCached = false
        }
        return isCached
    }

    fun clear(imageView: ImageView) {
        if (imageView.context is Activity && (imageView.context as Activity).isDestroyed) {
            return
        }

        GlideApp.with(imageView.context).clear(imageView)
    }

    fun setBackgroundImage(view: View, url: String) {
        val viewRef = WeakReference(view)
        GlideApp.with(view.context)
            .asBitmap()
            .load(url)
            .into(object : ViewBackgroundTarget<Bitmap>(view) {
                override fun setResource(resource: Bitmap) {
                    viewRef.get()?.background = BitmapDrawable(view.resources, resource)
                }
            })
    }

    fun notifyWhenImageLoaded(
        context: Context,
        url: String,
        notificationBuilder: NotificationCompat.Builder,
        expandedText: String?,
        notificationManager: NotificationManagerCompat,
        notificationId: Int
    ) {

        Handler(Looper.getMainLooper()).post {

            GlideApp.with(context)
                .asBitmap().load(url)
                .listener(object : GlideRequestListener<Bitmap> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        notificationManager.notify(
                            notificationId,
                            notificationBuilder.build()
                        )

                        return false
                    }

                    override fun onResourceReady(
                        bitmap: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onImageLoaded(
                            bitmap,
                            expandedText,
                            notificationId,
                            notificationBuilder,
                            notificationManager
                        )
                        return false
                    }
                }
                ).submit()
        }
    }

    suspend fun getBitmapImage(
        context: Context,
        imageUrl: String,
    ): Bitmap {
        return GlideApp.with(context)
            .asBitmap()
            .load(imageUrl)
            .submit()
            .get()
    }

    private fun onImageLoaded(
        resource: Bitmap?,
        expandedText: String?,
        notificationId: Int,
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManagerCompat
    ) {
        with(notificationBuilder) {
            setStyle(
                NotificationCompat.BigPictureStyle()
                    .setSummaryText(expandedText)
                    .bigPicture(resource)
                    .bigLargeIcon(null)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                setLargeIcon(resource)
            }
            notificationManager.notify(
                notificationId,
                build()
            )
        }
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
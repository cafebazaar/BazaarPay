package ir.cafebazaar.bazaarpay.utils.imageloader

import android.app.ActivityManager
import android.content.Context
import androidx.core.app.ActivityManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import ir.cafebazaar.bazaarpay.extensions.isHighPerformingDevice
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarGlideModule.Companion.AVAILABLE_MEMORY
import ir.cafebazaar.bazaarpay.utils.imageloader.BazaarGlideModule.Companion.AVAILABLE_PROCESSORS

@GlideModule
internal class BazaarGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        if (context.isHighPerformingDevice()) {
            glide.setMemoryCategory(MemoryCategory.NORMAL)
        } else {
            glide.setMemoryCategory(MemoryCategory.LOW)
        }
        super.registerComponents(context, glide, registry)
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        if (context.isHighPerformingDevice()) {
            builder.setDefaultRequestOptions(
                RequestOptions()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
        } else {
            builder.setDefaultRequestOptions(
                RequestOptions()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
        }
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    companion object {

        const val AVAILABLE_PROCESSORS = 4
        const val AVAILABLE_MEMORY = 256
    }
}
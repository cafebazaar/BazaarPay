package ir.cafebazaar.bazaarpay.network.dynamicrestclient

import android.content.Context
import okhttp3.Cache
import java.io.File

fun Context.getDefaultCache(): Cache {
    File(this.applicationContext.cacheDir, "http-cache").also {
        return Cache(it, (10 * 1024 * 1024).toLong())
    }
}
@file:Suppress("DEPRECATION")

package ir.cafebazaar.bazaarpay.network

import com.google.gson.GsonBuilder
import ir.cafebazaar.bazaarpay.network.gson.hook.Hooks
import ir.cafebazaar.bazaarpay.network.gson.unwrapper.DefaultUnwrapper
import ir.cafebazaar.bazaarpay.network.gson.withSweep
import retrofit2.converter.gson.GsonConverterFactory

fun gsonConverterFactory(): GsonConverterFactory {
    return GsonConverterFactory.create(
        GsonBuilder().withSweep {
            defaultUnwrapper = object : DefaultUnwrapper {
                override fun force() = true
                override fun <T> unwrapWith(type: Class<T>) = "singleReply.*"
            }
            hooks = object : Hooks {
                override fun <T> addToRoot(value: T): Pair<String, Any>? {
                    return Pair(
                        "properties",
                        "{asd}"
                    )
                }
            }
        }.create()
    )
}
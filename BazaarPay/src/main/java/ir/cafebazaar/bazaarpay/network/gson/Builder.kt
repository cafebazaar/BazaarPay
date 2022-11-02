package ir.cafebazaar.bazaarpay.network.gson

import com.google.gson.GsonBuilder
import ir.cafebazaar.bazaarpay.network.gson.hook.Hooks
import ir.cafebazaar.bazaarpay.network.gson.unwrapper.DefaultUnwrapper
import ir.cafebazaar.bazaarpay.network.gson.unwrapper.UnwrapperTypeAdapterFactory
import ir.cafebazaar.bazaarpay.network.gson.wrapper.DefaultWrapper
import ir.cafebazaar.bazaarpay.network.gson.wrapper.WrapperTypeAdapterFactory

class Builder internal constructor(private val gsonBuilder: GsonBuilder) {

    private val disabledWrapper = object : DefaultWrapper {
        override fun <T> wrapWith(value: T): String? = null
    }

    private val disabledUnwrapper = object : DefaultUnwrapper {
        override fun <T> unwrapWith(type: Class<T>): String? = null
    }

    private val disabledHooks = object : Hooks {}

    var defaultWrapper: DefaultWrapper = disabledWrapper
    var defaultUnwrapper: DefaultUnwrapper = disabledUnwrapper
    var hooks: Hooks = disabledHooks

    fun build(): GsonBuilder {

        val wrapperTypeAdapterFactory = WrapperTypeAdapterFactory(defaultWrapper, hooks)

        val unwrapperTypeAdapterFactory = UnwrapperTypeAdapterFactory(defaultUnwrapper)

        gsonBuilder.registerTypeAdapterFactory(wrapperTypeAdapterFactory)
        gsonBuilder.registerTypeAdapterFactory(unwrapperTypeAdapterFactory)

        return gsonBuilder
    }
}
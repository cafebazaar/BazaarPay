package ir.cafebazaar.bazaarpay.network.gson.wrapper

import ir.cafebazaar.bazaarpay.network.gson.SweepTypeAdapterFactory
import ir.cafebazaar.bazaarpay.network.gson.SweepWrapper
import ir.cafebazaar.bazaarpay.network.gson.hook.Hooks
import ir.cafebazaar.bazaarpay.network.gson.hook.HooksDelegation
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import ir.cafebazaar.bazaarpay.network.model.SweepReflection.isAnnotationPresent

internal class WrapperTypeAdapterFactory(
    defaultWrapper: DefaultWrapper,
    hooks: Hooks
) : SweepTypeAdapterFactory {

    private val wrapperNameBuilder = WrapperNamesBuilder(defaultWrapper)
    private val hooksDelegation = HooksDelegation(hooks)

    override fun <T> create(
        gson: Gson,
        type: TypeToken<T>,
        delegate: TypeAdapter<T>,
        elementAdapter: TypeAdapter<JsonElement>
    ): TypeAdapter<T> {
        if (isAnnotationPresent(type.rawType, SweepWrapper::class.java)) {
            return WrapperTypeAdapter(
                gson,
                delegate,
                elementAdapter,
                type,
                wrapperNameBuilder,
                hooksDelegation
            )
        }
        return delegate
    }
}
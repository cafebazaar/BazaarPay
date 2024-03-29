package ir.cafebazaar.bazaarpay.network.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * An implementation of [TypeAdapter] which passes (de)serializing to the delegated adapter.
 */
internal abstract class SweepTypeAdapter<T>(
    protected val gson: Gson,
    protected val delegate: TypeAdapter<T>,
    protected val elementAdapter: TypeAdapter<JsonElement>,
    protected val type: TypeToken<T>
) : TypeAdapter<T>() {

    override fun write(out: JsonWriter, value: T) {
        delegate.write(out, value)
    }

    override fun read(reader: JsonReader): T {
        return delegate.fromJsonTree(elementAdapter.read(reader))
    }
}
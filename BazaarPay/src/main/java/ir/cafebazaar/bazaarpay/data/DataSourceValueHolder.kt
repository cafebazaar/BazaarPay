package ir.cafebazaar.bazaarpay.data

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class DataSourceValueHolder<T>(
    private val sharedDataSource: SharedDataSource,
    private val key: String,
    private val rejectValue: T
) : ReadWriteProperty<Any?, T> {

    private var value: T = rejectValue
    private var isLoaded = false

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!isLoaded) {
            isLoaded = true
            value = sharedDataSource.get(key, rejectValue)
        }
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        sharedDataSource.put(key, value)
        this.value = value
    }
}
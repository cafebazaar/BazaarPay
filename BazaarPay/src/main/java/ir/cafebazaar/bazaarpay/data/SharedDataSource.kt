package ir.cafebazaar.bazaarpay.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import ir.cafebazaar.bazaarpay.ServiceLocator

internal abstract class SharedDataSource {

    private val context: Context = ServiceLocator.get()

    abstract val fileName: String

    private val sharedPreference by lazy {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T) = when (defaultValue) {
        is String -> sharedPreference.getString(key, defaultValue) as T
        is Int -> sharedPreference.getInt(key, defaultValue) as T
        is Float -> sharedPreference.getFloat(key, defaultValue) as T
        is Double -> sharedPreference.getFloat(key, defaultValue.toFloat()) as T
        is Long -> sharedPreference.getLong(key, defaultValue) as T
        is Boolean -> sharedPreference.getBoolean(key, defaultValue) as T
        else -> throw IllegalArgumentException("getValue Type of value is not supported")
    }

    @SuppressLint("ApplySharedPref")
    fun <T> put(key: String, value: T, commit: Boolean = false) {
        with(sharedPreference.edit()) {
            putOnly(key, value)
            if (commit) {
                commit()
            } else {
                apply()
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    fun put(keyValues: Map<String, Any>, commit: Boolean = false) {
        with(sharedPreference.edit()) {
            keyValues.forEach { (key, value) ->
                putOnly(key, value)
            }
            if (commit) {
                commit()
            } else {
                apply()
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    fun remove(key: String, commit: Boolean = false) {
        with(sharedPreference.edit()) {
            remove(key)
            if (commit) {
                commit()
            } else {
                apply()
            }
        }
    }

    private fun <T> SharedPreferences.Editor.putOnly(
        key: String,
        value: T
    ): SharedPreferences.Editor {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Double -> putFloat(key, value.toFloat())
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            else -> throw IllegalArgumentException("putOnly Type of value is not supported")
        }
        return this
    }

    fun isKeyExists(key: String) = sharedPreference.contains(key)

    fun clearAll() {
        sharedPreference.edit().clear().apply()
    }
}
package ir.cafebazaar.bazaarpay.data.config

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.DataSourceValueHolder
import ir.cafebazaar.bazaarpay.data.SharedDataSource
import ir.cafebazaar.bazaarpay.data.config.model.Config

internal class ConfigLocalDataSource {

    private val deviceSharedDataSource: SharedDataSource = ServiceLocator.get(ServiceLocator.DEVICE)

    private var configJson: String by DataSourceValueHolder(
        deviceSharedDataSource,
        CONFIG_JSON,
        rejectValue = "",
    )

    private var configLastFetchTime: Long by DataSourceValueHolder(
        deviceSharedDataSource,
        CONFIG_LAST_FETCH_TIME,
        rejectValue = 0,
    )

    fun saveConfig(config: Config) {
        configJson = Gson().toJson(config)
        configLastFetchTime = System.currentTimeMillis()
    }

    fun getConfig(): Config? {
        return try {
            val json = configJson
            if (json.isNotEmpty()) {
                Gson().fromJson(json, Config::class.java)
            } else {
                null
            }
        } catch (ignore: JsonSyntaxException) {
            null
        }
    }

    fun shouldFetchNewConfig(): Boolean {
        val config = getConfig()
        val time = System.currentTimeMillis() - configLastFetchTime
        return config == null || time >= config.concierge.ttl
    }

    private companion object {

        const val CONFIG_JSON = "config_json"
        const val CONFIG_LAST_FETCH_TIME = "config_fetch_time"
    }
}
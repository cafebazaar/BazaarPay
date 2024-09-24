package ir.cafebazaar.bazaarpay.data.config

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.config.model.Config
import ir.cafebazaar.bazaarpay.utils.doOnSuccess

internal class ConfigRepository {

    private val configRemoteDataSource: ConfigRemoteDataSource = ServiceLocator.get()
    private val configLocalDataSource: ConfigLocalDataSource = ServiceLocator.get()

    fun getConfig(): Config? {
        return configLocalDataSource.getConfig()
    }

    suspend fun fetchConfig() {
        if (configLocalDataSource.shouldFetchNewConfig()) {
            configRemoteDataSource.getConfig()
                .doOnSuccess { config ->
                    configLocalDataSource.saveConfig(config)
                }
        }
    }
}
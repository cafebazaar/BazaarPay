package ir.cafebazaar.bazaarpay.data.config

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.config.api.ConfigService
import ir.cafebazaar.bazaarpay.data.config.model.Config
import ir.cafebazaar.bazaarpay.extensions.ServiceType
import ir.cafebazaar.bazaarpay.extensions.safeApiCall
import ir.cafebazaar.bazaarpay.models.GlobalDispatchers
import ir.cafebazaar.bazaarpay.utils.Either
import kotlinx.coroutines.withContext

internal class ConfigRemoteDataSource {

    private val configService: ConfigService by lazy { ServiceLocator.get() }
    private val globalDispatchers: GlobalDispatchers by lazy { ServiceLocator.get() }

    suspend fun getConfig(): Either<Config> {
        return withContext(globalDispatchers.iO) {
            return@withContext safeApiCall(ServiceType.BAZAARPAY) {
                configService.getConfig().toConfig()
            }
        }
    }
}
package ir.cafebazaar.bazaarpay.network.dynamicrestclient.client

import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Interceptor
import retrofit2.Converter
import java.util.ArrayList

internal class ClientBuilderImplement : ClientBuilder {

    private var authenticator: Authenticator? = null

    private var interceptors: MutableList<Interceptor> = ArrayList()

    private var converterFactories: MutableList<Converter.Factory> = ArrayList()

    private var debugMode = false

    private var cache: Cache? = null

    override fun withAuthenticator(authenticator: Authenticator): ClientBuilderImplement {
        return this.apply { this.authenticator = authenticator }
    }

    override fun withInterceptors(interceptors: List<Interceptor>): ClientBuilderImplement {
        return this.apply {
            for (interceptor in interceptors)
                this.interceptors.add(interceptor)
        }
    }

    override fun withInterceptor(interceptor: Interceptor): ClientBuilderImplement {
        return this.apply { interceptors.add(interceptor) }
    }

    override fun withConverterFactories(converterFactories: List<Converter.Factory>): ClientBuilderImplement {
        return this.apply {
            for (converterFactory in converterFactories)
                this.converterFactories.add(converterFactory)
        }
    }

    override fun withConverterFactory(converterFactory: Converter.Factory): ClientBuilderImplement {
        return this.apply { converterFactories.add(converterFactory) }
    }

    override fun withDebugMode(debugMode: Boolean): ClientBuilderImplement {
        return this.apply { this.debugMode = debugMode }
    }

    override fun withCache(cache: Cache): ClientBuilderImplement {
        return this.apply { this.cache = cache }
    }

    override fun build(): Client {
        return ClientImplement(
            authenticator,
            interceptors,
            converterFactories,
            cache,
            debugMode
        )
    }
}
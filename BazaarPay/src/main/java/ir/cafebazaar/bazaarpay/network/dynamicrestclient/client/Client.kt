package ir.cafebazaar.bazaarpay.network.dynamicrestclient.client

import ir.cafebazaar.bazaarpay.network.dynamicrestclient.base.Base

interface Client {

    fun cancelAllRequests()

    fun buildBase(
        baseUrl: String,
        defaultHeaders: HashMap<String, Any?>? = null
    ): Base

    companion object {
        fun builder(): ClientBuilder = ClientBuilderImplement()
    }
}
package ir.cafebazaar.bazaarpay.data.bazaar.models

import ir.cafebazaar.bazaarpay.ServiceLocator

internal abstract class BazaarBaseRequest(
    val properties: Map<String, Any> = mapOf(
        "language" to ServiceLocator.get<Int>(ServiceLocator.LANGUAGE),
        "androidClientInfo" to AndroidClientInfo(ServiceLocator.get(ServiceLocator.LANGUAGE))
    )
)

internal data class AndroidClientInfo(val locale: String)
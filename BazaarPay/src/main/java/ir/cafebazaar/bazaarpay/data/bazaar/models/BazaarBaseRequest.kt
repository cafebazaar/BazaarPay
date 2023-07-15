package ir.cafebazaar.bazaarpay.data.bazaar.models

import ir.cafebazaar.bazaarpay.ServiceLocator.FA_LANGUAGE

internal abstract class BazaarBaseRequest(
    val properties: Map<String, Any> = mapOf(
        "language" to FA_LANGUAGE,
        "androidClientInfo" to AndroidClientInfo("fa")
    )
)

internal data class AndroidClientInfo(val locale: String)
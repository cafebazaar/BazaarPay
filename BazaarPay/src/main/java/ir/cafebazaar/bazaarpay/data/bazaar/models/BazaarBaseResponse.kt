package ir.cafebazaar.bazaarpay.data.bazaar.models

import ir.hamidbazargan.dynamicrestclient.response.GeneralResponseModel

internal open class BazaarBaseResponse(
    val properties: ResponseProperties? = null
) : GeneralResponseModel()
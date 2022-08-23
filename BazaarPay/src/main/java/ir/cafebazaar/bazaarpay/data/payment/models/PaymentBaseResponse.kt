package ir.cafebazaar.bazaarpay.data.payment.models

import ir.hamidbazargan.dynamicrestclient.response.GeneralResponseModel

internal open class PaymentBaseResponse: GeneralResponseModel() {
    val detail: String? = null
}
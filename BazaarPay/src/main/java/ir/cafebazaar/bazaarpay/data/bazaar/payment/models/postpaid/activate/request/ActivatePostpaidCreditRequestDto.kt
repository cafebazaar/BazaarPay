package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.postpaid.activate.request

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class ActivatePostpaidCreditSingleRequest : BazaarBaseRequest() {

    val singleRequest: ActivatePostpaidCreditRequest =
        ActivatePostpaidCreditRequest(
            ActivatePostpaidCreditRequestBody
        )
}

internal class ActivatePostpaidCreditRequest(
    val activatePostpaidCreditRequest: ActivatePostpaidCreditRequestBody
)

internal object ActivatePostpaidCreditRequestBody
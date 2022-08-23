package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.request

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class GetAvailableBanksSingleRequest : BazaarBaseRequest() {

    val singleRequest: GetAvailableBanksRequest =
        GetAvailableBanksRequest(
            GetAvailableBanksRequestBody
        )
}

internal class GetAvailableBanksRequest(
    val getAvailableBanksRequest: GetAvailableBanksRequestBody
)

internal object GetAvailableBanksRequestBody
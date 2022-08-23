package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.request

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseRequest

internal class GetDirectDebitOnBoardingSingleRequest: BazaarBaseRequest() {

    val singleRequest: GetDirectDebitOnBoardingRequest =
        GetDirectDebitOnBoardingRequest(GetDirectDebitOnBoardingRequestBody)
}

internal class GetDirectDebitOnBoardingRequest(
    val getDirectDebitOnBoardingRequest : GetDirectDebitOnBoardingRequestBody
)

internal object GetDirectDebitOnBoardingRequestBody
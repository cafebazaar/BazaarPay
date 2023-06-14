package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.request

import com.google.gson.annotations.SerializedName

internal data class GetDirectDebitOnBoardingSingleRequest(
    @SerializedName("checkout_token") val checkoutToken: String
)
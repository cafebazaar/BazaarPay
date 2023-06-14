package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.request

import com.google.gson.annotations.SerializedName

internal class GetAvailableBanksSingleRequestDto(
    @SerializedName("checkout_token") val checkoutToken: String
)

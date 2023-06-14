package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.request

import com.google.gson.annotations.SerializedName

internal data class GetDirectDebitContractCreationUrlSingleRequest(
    @SerializedName("bank") val bankCode: String,
    @SerializedName("national_id") val nationalId: String,
    @SerializedName("redirect_url") val redirectUrl: String,
    @SerializedName("checkout_token") val checkoutToken: String,
    @SerializedName("source") val source: Int,
)
package ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.payment.models.PaymentBaseResponse
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.MerchantInfo

internal data class MerchantInfoDto(
    @SerializedName("account_name") val accountName: String?,
    @SerializedName("logo_url") val logoUrl: String?,
    @SerializedName("account_name_en") val englishAccountName: String?
): PaymentBaseResponse() {

    fun toMerchantInfo(): MerchantInfo {
        return MerchantInfo(
            accountName,
            logoUrl,
            englishAccountName
        )
    }
}
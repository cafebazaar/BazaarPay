package ir.cafebazaar.bazaarpay.data.payment.models.pay.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.DynamicCreditOption
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.response.OptionDto

internal data class IncreaseBalanceOptionsResponseDto(
    @SerializedName("default_option") val defaultAmount: Long,
    @SerializedName("min_available_amount") val minAvailableAmount: Long,
    @SerializedName("max_available_amount") val maxAvailableAmount: Long,
    @SerializedName("description") val description: String,
    @SerializedName("balance_string") val userBalanceString: String,
    @SerializedName("balance") val userBalance: Long,
    @SerializedName("options") val optionDtos: List<OptionDto>
) {
    fun toDynamicCreditOption(): DynamicCreditOption {
        return DynamicCreditOption(
            defaultAmount,
            minAvailableAmount,
            maxAvailableAmount,
            description,
            userBalanceString,
            userBalance,
            optionDtos.map { it.toOption() }
        )
    }
}
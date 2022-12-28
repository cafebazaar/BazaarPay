package ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.payment.models.PaymentBaseResponse
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.DynamicCreditOption
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.Option
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethod
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.PaymentMethodsInfo

internal data class PaymentMethodsInfoDto(
    @SerializedName("destination_title") val destinationTitle: String,
    @SerializedName("amount") val amount: Long?,
    @SerializedName("payment_methods") val paymentMethodDtos: List<PaymentMethodDto>,
    @SerializedName("dynamic_credit_option") val dynamicCreditOptionDto: DynamicCreditOptionDto?

): PaymentBaseResponse() {

    fun toPaymentMethodInfo(): PaymentMethodsInfo {
        return PaymentMethodsInfo(
            destinationTitle,
            amount,
            paymentMethodDtos.map { it.toPaymentMethodItem() },
            dynamicCreditOptionDto?.toDynamicCreditOption()
        )
    }
}

internal data class PaymentMethodDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("sub_description") val subDescription: String?,
    @SerializedName("method_type") val methodType: String,
    @SerializedName("icon_url") val iconUrl: String?,
    @SerializedName("price_string") val priceString: String?,
    @SerializedName("enabled") val enabled: Boolean?
) {

    fun toPaymentMethodItem(): PaymentMethod {
        return PaymentMethod(
            title,
            description,
            subDescription,
            PaymentMethodsType.valueOf(methodType.uppercase()),
            iconUrl,
            priceString,
            enabled = enabled ?: true
        )
    }
}

internal data class DynamicCreditOptionDto(
    @SerializedName("default_amount") val defaultAmount: Long ,
    @SerializedName("min_available_amount") val minAvailableAmount: Long ,
    @SerializedName("max_available_amount") val maxAvailableAmount: Long ,
    @SerializedName("description") val description: String ,
    @SerializedName("user_balance_string") val userBalanceString: String ,
    @SerializedName("user_balance") val userBalance: Long ,
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

internal data class OptionDto(
    @SerializedName("label") val label: String ,
    @SerializedName("amount") val amount: Long
) {

    fun toOption(): Option {
        return Option(
            label,
            amount
        )
    }
}
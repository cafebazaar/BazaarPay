package ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods

import android.os.Parcelable
import ir.cafebazaar.bazaarpay.screens.payment.paymentmethods.PaymentMethodsType
import kotlinx.parcelize.Parcelize

internal typealias PaymentMethodItems = List<PaymentMethod>

internal data class PaymentMethodsInfo(
    val destinationTitle: String,
    val amount: Long?,
    val paymentMethods: List<PaymentMethod>,
    val dynamicCreditOption: DynamicCreditOption?
)

internal data class PaymentMethod(
    val title: String,
    val description: String?,
    val subDescription: String?,
    val methodType: PaymentMethodsType,
    val methodTypeString: String,
    val iconUrl: String?,
    val priceString: String?,
    val enabled: Boolean = true
)

@Parcelize
data class DynamicCreditOption(
    val defaultAmount: Long,
    val minAvailableAmount: Long,
    val maxAvailableAmount: Long,
    val description: String,
    val userBalanceString: String,
    val userBalance: Long,
    val options: List<Option>
) : Parcelable

@Parcelize
data class Option(
    val label: String,
    val amount: Long,
    var isSelected: Boolean = false
) : Parcelable
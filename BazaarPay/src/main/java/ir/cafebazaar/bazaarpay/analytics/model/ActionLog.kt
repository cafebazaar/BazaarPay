package ir.cafebazaar.bazaarpay.analytics.model

import ir.cafebazaar.bazaarpay.data.analytics.model.ActionLogDto
import ir.cafebazaar.bazaarpay.data.analytics.model.PaymentFlowDetailsDto

internal data class ActionLog(
    val id: Long,
    val type: EventType,
    val sessionId: String,
    val timestamp: Long,
    val where: String?,
    val actionDetails: String?,
    val paymentFlowDetails: PaymentFlowDetails,
    val pageDetails: String?,
    val extra: String?,
)

internal data class PaymentFlowDetails(
    val checkOutToken: String?,
    val merchantName: String?,
    val amount: String?
)

internal fun PaymentFlowDetails.toPaymentFlowDetailsDto(): PaymentFlowDetailsDto {
    return PaymentFlowDetailsDto(
        checkOutToken = checkOutToken,
        merchantName = merchantName,
        amount = amount
    )
}

internal fun ActionLog.toActionLogDto(
    source: String,
    deviceId: String
): ActionLogDto {
    return ActionLogDto(
        id = id,
        source = source,
        type = type,
        traceId = sessionId,
        timestamp = timestamp,
        deviceId = deviceId,
        where = where,
        actionDetails = actionDetails,
        extra = extra,
        paymentFlowDetailsDto = paymentFlowDetails.toPaymentFlowDetailsDto()
    )
}

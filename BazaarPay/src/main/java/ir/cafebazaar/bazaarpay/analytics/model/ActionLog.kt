package ir.cafebazaar.bazaarpay.analytics.model

internal data class ActionLog(
    val id: Long,
    val type: EventType,
    val timestamp: Long,
    val where: String?,
    val extra: String?,
)

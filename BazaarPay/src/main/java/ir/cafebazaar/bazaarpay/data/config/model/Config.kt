package ir.cafebazaar.bazaarpay.data.config.model

internal data class Config(
    val actionLog: ActionLogConfig,
    val concierge: ConciergeConfig,
)

internal data class ActionLogConfig(
    val batchSize: Int,
    val retryCount: Int,
    val validators: List<String>,
)

internal data class ConciergeConfig(
    val ttl: Int,
)
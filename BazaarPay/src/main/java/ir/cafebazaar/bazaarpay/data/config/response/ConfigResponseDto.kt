package ir.cafebazaar.bazaarpay.data.config.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.config.model.ActionLogConfig
import ir.cafebazaar.bazaarpay.data.config.model.ConciergeConfig
import ir.cafebazaar.bazaarpay.data.config.model.Config
import ir.cafebazaar.bazaarpay.data.payment.models.PaymentBaseResponse

internal data class ConfigResponseDto(
    @SerializedName("action_log") val actionLog: ActionLogConfigDto,
    @SerializedName("concierge") val concierge: ConciergeConfigDto,
) : PaymentBaseResponse() {

    fun toConfig(): Config {
        return Config(
            actionLog = actionLog.toActionLogConfig(),
            concierge = concierge.toConciergeConfig(),
        )
    }
}

internal data class ActionLogConfigDto(
    @SerializedName("batch_size") val batchSize: Int,
    @SerializedName("retry_count") val retryCount: Int,
    @SerializedName("valid_urls") val validators: List<String>,
) {

    fun toActionLogConfig(): ActionLogConfig {
        return ActionLogConfig(
            batchSize = batchSize,
            retryCount = retryCount,
            validators = validators,
        )
    }
}

internal data class ConciergeConfigDto(
    @SerializedName("ttl") val ttl: Int,
) {

    fun toConciergeConfig(): ConciergeConfig {
        return ConciergeConfig(
            ttl = ttl,
        )
    }
}
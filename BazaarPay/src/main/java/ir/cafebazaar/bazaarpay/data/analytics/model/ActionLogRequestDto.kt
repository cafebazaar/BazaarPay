package ir.cafebazaar.bazaarpay.data.analytics.model

import com.google.gson.annotations.SerializedName

internal data class ActionLogRequestDto(
    @SerializedName("events") val events: List<ActionLogDto>
)
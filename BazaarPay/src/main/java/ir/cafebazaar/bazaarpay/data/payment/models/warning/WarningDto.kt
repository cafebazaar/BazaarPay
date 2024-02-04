package ir.cafebazaar.bazaarpay.data.payment.models.warning

import com.google.gson.annotations.SerializedName

data class WarningDto(
    @SerializedName("text") val text: String,
    @SerializedName("light_icon_url") val lightIcon: String,
    @SerializedName("dark_icon_url") val darkIcon: String,
    @SerializedName("action_text") val actionText: String,
)

fun WarningDto.toWarning(): Warning {
    return Warning(text = text, lightIcon = lightIcon, darkIcon = darkIcon, actionText = actionText)
}

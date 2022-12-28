package ir.cafebazaar.bazaarpay.models

import com.google.gson.annotations.SerializedName

data class BazaarErrorResponseDto(
    @SerializedName("properties") val properties: PropertiesResponseDto? = null
)

data class PropertiesResponseDto(
    @SerializedName("errorMessage") val errorMessage: String,
    @SerializedName("statusCode") val errorCode: Int
)

data class BazaarPayErrorResponseDto(
    @SerializedName("detail") val detail: String
)
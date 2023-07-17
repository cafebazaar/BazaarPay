package ir.cafebazaar.bazaarpay.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.util.Objects

data class BazaarErrorResponseDto(
    @SerializedName("properties") val properties: PropertiesResponseDto? = null
)

data class PropertiesResponseDto(
    @SerializedName("errorMessage") val errorMessage: String,
    @SerializedName("statusCode") val errorCode: Int
)

data class BazaarPayErrorResponseDto(
    @SerializedName(ERROR_KEY) val error: Objects?,
    @Expose(serialize = false, deserialize = false) val detail: String
)

internal class ErrorDeserializer : JsonDeserializer<BazaarPayErrorResponseDto> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BazaarPayErrorResponseDto {
        val jsonObject = json?.asJsonObject
        val errorMessage = if (jsonObject?.get(ERROR_KEY)?.isJsonArray == true) {
            jsonObject.get(ERROR_KEY).asJsonArray.toList().joinToString(",")
        } else if (jsonObject?.get(ERROR_KEY)?.isJsonPrimitive == true) {
            jsonObject.get(ERROR_KEY).asString
        } else {
            ""
        }
        return BazaarPayErrorResponseDto(null, errorMessage)
    }
}

private const val ERROR_KEY = "detail"
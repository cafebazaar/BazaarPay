package ir.cafebazaar.bazaarpay.network.dynamicrestclient.response

import okhttp3.ResponseBody
import retrofit2.Response

data class ResponseModel<E : GeneralResponseModel>(
    val response: Response<ResponseBody?>,
    val httpCode: Int,
    var rawResponse: String,
    val data: E
)
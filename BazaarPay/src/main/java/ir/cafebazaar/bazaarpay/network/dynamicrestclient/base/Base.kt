package ir.cafebazaar.bazaarpay.network.dynamicrestclient.base

import ir.cafebazaar.bazaarpay.network.dynamicrestclient.request.RequestBuilder
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.response.GeneralResponseModel

interface Base {

    var baseUrl: String

    var defaultHeaders: HashMap<String, Any?>

    fun <E : GeneralResponseModel> getMethod(
        responseModel: Class<E>,
        path: String = ""
    ): RequestBuilder<E>

    fun <E : GeneralResponseModel> postMethod(
        responseModel: Class<E>,
        path: String = ""
    ): RequestBuilder<E>

    fun <E : GeneralResponseModel> putMethod(
        responseModel: Class<E>,
        path: String = ""
    ): RequestBuilder<E>

    fun <E : GeneralResponseModel> patchMethod(
        responseModel: Class<E>,
        path: String = ""
    ): RequestBuilder<E>

    fun <E : GeneralResponseModel> deleteMethod(
        responseModel: Class<E>,
        path: String = ""
    ): RequestBuilder<E>
}
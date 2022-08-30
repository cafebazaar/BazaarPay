package ir.cafebazaar.bazaarpay.network.dynamicrestclient.base

import ir.cafebazaar.bazaarpay.network.dynamicrestclient.base.Base
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.ApiInterface
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.request.HttpRequestMethods
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.request.RequestBuilderImplement
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.client.ClientImplement
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.response.GeneralResponseModel
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.request.RequestBuilder
import java.lang.Exception
import kotlin.collections.HashMap

internal class BaseImplement(
    private val client: ClientImplement,
    baseUrl: String,
    defaultHeaders: HashMap<String, Any?>?
) : Base {

    override var baseUrl: String = ""
        set(value) {
            if (value.isEmpty()) {
                throw Exception(BASE_URL_EXCEPTION_MESSAGE)
            }
            field = value
        }

    override var defaultHeaders: HashMap<String, Any?> = HashMap()
        set(value) {
            if (value.isEmpty()) {
                throw Exception(DEFAULT_HEADERS_EXCEPTION_MESSAGE)
            }
            field = value
        }


    val restClient: ApiInterface
        get() {
            println("@@@@@@@@ get")
            return client.getRestClient(baseUrl)
        }

    init {
        if (baseUrl.isEmpty().not()) {
            this.baseUrl = baseUrl
        }
        if (defaultHeaders?.isEmpty()?.not() == true) {
            this.defaultHeaders = defaultHeaders
        }
    }

    override fun <E : GeneralResponseModel> getMethod(
        responseModel: Class<E>,
        path: String
    ): RequestBuilder<E> {
        return createRequestBuilderWithBaseConfig(
            responseModel,
            HttpRequestMethods.GET,
            path
        )
    }

    override fun <E : GeneralResponseModel> postMethod(
        responseModel: Class<E>,
        path: String
    ): RequestBuilder<E> {
        return createRequestBuilderWithBaseConfig(
            responseModel,
            HttpRequestMethods.POST,
            path
        )
    }

    override fun <E : GeneralResponseModel> putMethod(
        responseModel: Class<E>,
        path: String
    ): RequestBuilder<E> {
        return createRequestBuilderWithBaseConfig(
            responseModel,
            HttpRequestMethods.PUT,
            path
        )
    }

    override fun <E : GeneralResponseModel> patchMethod(
        responseModel: Class<E>,
        path: String
    ): RequestBuilder<E> {
        return createRequestBuilderWithBaseConfig(
            responseModel,
            HttpRequestMethods.PATCH,
            path
        )
    }

    override fun <E : GeneralResponseModel> deleteMethod(
        responseModel: Class<E>,
        path: String
    ): RequestBuilder<E> {
        return createRequestBuilderWithBaseConfig(
            responseModel,
            HttpRequestMethods.DELETE,
            path
        )
    }

    private fun <E : GeneralResponseModel> createRequestBuilderWithBaseConfig(
        responseModel: Class<E>,
        httpRequestMethod: HttpRequestMethods,
        path: String
    ): RequestBuilder<E> {
        return RequestBuilderImplement<E>(
            base = this,
            httpRequestMethod,
            path
        ).apply {
            setResponseModel(responseModel)
            addHeaders(defaultHeaders)
        }
    }

    private companion object {
        const val BASE_URL_EXCEPTION_MESSAGE =
            "Base url for base has some errors"
        const val DEFAULT_HEADERS_EXCEPTION_MESSAGE =
            "Default headers can't be null or empty"
    }
}
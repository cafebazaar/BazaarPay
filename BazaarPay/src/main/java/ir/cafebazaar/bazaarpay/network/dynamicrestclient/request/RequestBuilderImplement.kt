package ir.cafebazaar.bazaarpay.network.dynamicrestclient.request

import ir.cafebazaar.bazaarpay.network.dynamicrestclient.ApiInterface
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.CustomParser
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.client.RestClient
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.response.GeneralResponseModel
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.response.ResponseModel
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.base.BaseImplement
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.request.HttpRequestMethods
import ir.cafebazaar.bazaarpay.network.dynamicrestclient.request.RequestBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import kotlin.collections.HashMap

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
internal class RequestBuilderImplement<E : GeneralResponseModel>(
    base: BaseImplement,
    private var httpRequestMethod: HttpRequestMethods = HttpRequestMethods.GET,
    path: String
) : RequestBuilder<E> {

    private var retrofitClient: ApiInterface = base.restClient

    private var requestUrl: String = base.baseUrl + path

    private var requestBody: RequestBody? = null

    private var formBody: HashMap<String, Any> = HashMap()

    private var headers: HashMap<String, Any> = HashMap()

    private var queryParameter: HashMap<String, Any> = HashMap()

    private var notEncoded = false

    private lateinit var responseModel: Class<E>

    private var customParser: CustomParser<E>? = null

    override fun setNotEncoded(): RequestBuilder<E> {
        return this.apply { notEncoded = true }
    }

    override fun setPostBody(postBody: Any): RequestBuilder<E> {
        return this.apply {
            requestBody = RestClient.provideGson().toJson(postBody).toRequestBody(JSON)
        }
    }

    override fun setResponseModel(responseModel: Class<E>): RequestBuilder<E> {
        return this.apply { this.responseModel = responseModel }
    }

    override fun setCustomParser(customParser: CustomParser<E>?): RequestBuilder<E> {
        return this.apply { this.customParser = customParser }
    }

    override fun addBodyParameters(requestBodyHashMap: HashMap<String, Any?>?): RequestBuilder<E> {
        return this.apply {
            requestBodyHashMap?.filter {
                it.value != null
            }?.mapValues {
                requireNotNull(it.value)
            }?.let {
                formBody.putAll(it)
            }
        }
    }

    override fun addBodyParameter(
        key: String,
        value: Any
    ): RequestBuilder<E> {
        return this.apply {
            formBody[key] = value
        }
    }

    override fun addHeaders(headersMap: HashMap<String, Any?>?): RequestBuilder<E> {
        return this.apply {
            headersMap?.filter {
                it.value != null
            }?.mapValues {
                requireNotNull(it.value)
            }?.let {
                headers.putAll(it)
            }
        }
    }

    override fun addHeader(key: String, value: Any): RequestBuilder<E> {
        return this.apply {
            headers[key] = value
        }
    }

    override fun addQueryParameters(queryParameters: HashMap<String, Any?>?): RequestBuilder<E> {
        return this.apply {
            queryParameters?.filter {
                it.value != null
            }?.mapValues {
                requireNotNull(it.value)
            }?.let {
                queryParameter.putAll(it)
            }
        }
    }

    override fun addQueryParameter(
        key: String,
        value: Any
    ): RequestBuilder<E> {
        return this.apply {
            queryParameter[key] = value
        }
    }

    override fun getResponse(): ResponseModel<E> {
        var finalResponse: ResponseModel<E>? = null
        runCatching {
            getRequestCall().execute()
        }.onSuccess {
            it.also { response ->
                if (response.isSuccessful) {
                    response.body()?.string()?.also { rawResponse ->
                        finalResponse = generateResponse(response, rawResponse)
                    }
                } else {
                    response.errorBody()?.string()?.also { rawResponse ->
                        finalResponse = generateResponse(response, rawResponse)
                    }
                }
            }
        }.onFailure {
            throw Exception(it.message, it)
        }
        return requireNotNull(finalResponse)
    }

    private fun generateResponse(
        response: Response<ResponseBody?>,
        rawResponse: String
    ): ResponseModel<E>? {
        customParser?.let { parser ->
            parser.parseData(response)?.also { data ->
                return ResponseModel(
                    response,
                    response.code(),
                    rawResponse,
                    data
                )
            } ?: parser.parseData(rawResponse)?.also { data ->
                return ResponseModel(
                    response,
                    response.code(),
                    rawResponse,
                    data
                )
            }
        } ?: run {
            RestClient.provideGson().also { gson ->
                gson.fromJson(
                    rawResponse,
                    responseModel
                ).also { data ->
                    return ResponseModel(
                        response,
                        response.code(),
                        rawResponse,
                        data
                    )
                }
            }
        }
        return null
    }

    private fun validateInputs() {
        if (requestUrl.isEmpty()) {
            throw Exception(BASE_URL_EXCEPTION_MESSAGE)
        }

        if (((httpRequestMethod == HttpRequestMethods.PATCH)
                .or(httpRequestMethod == HttpRequestMethods.POST)
                .or(httpRequestMethod == HttpRequestMethods.PUT))
                .and(requestBody == null)
                .and(formBody.isEmpty())
        ) {
            throw Exception(EMPTY_REQUEST_EXCEPTION_MESSAGE)
        }
    }

    private fun getRequestCall(): Call<ResponseBody?> {
        validateInputs()
        when (httpRequestMethod) {
            HttpRequestMethods.GET -> return retrofitClient.performGetRequest(
                requestUrl,
                headers,
                queryParameter
            )
            HttpRequestMethods.POST -> {
                requestBody?.let {
                    return retrofitClient.performPostRequest(
                        requestUrl,
                        headers,
                        it,
                        queryParameter
                    )
                }
                formBody.isNotEmpty().run {
                    if (notEncoded) {
                        return retrofitClient.performPostRequestNotEncoded(
                            requestUrl,
                            headers,
                            formBody,
                            queryParameter
                        )
                    } else {
                        return retrofitClient.performPostRequest(
                            requestUrl,
                            headers,
                            formBody,
                            queryParameter
                        )
                    }
                }
            }
            HttpRequestMethods.PATCH -> {
                requestBody?.let {
                    return retrofitClient.performPatchRequest(
                        requestUrl,
                        headers,
                        it,
                        queryParameter
                    )
                }
                formBody.isNotEmpty().run {
                    if (notEncoded) {
                        return retrofitClient.performPatchRequestNotEncoded(
                            requestUrl,
                            headers,
                            formBody,
                            queryParameter
                        )
                    } else {
                        return retrofitClient.performPatchRequest(
                            requestUrl,
                            headers,
                            formBody,
                            queryParameter
                        )
                    }
                }
            }
            HttpRequestMethods.PUT -> {
                requestBody?.let {
                    return retrofitClient.performPutRequest(
                        requestUrl,
                        headers,
                        it,
                        queryParameter
                    )
                }
                formBody.isNotEmpty().run {
                    if (notEncoded) {
                        return retrofitClient.performPutRequestNotEncoded(
                            requestUrl,
                            headers,
                            formBody,
                            queryParameter
                        )
                    } else {
                        return retrofitClient.performPutRequest(
                            requestUrl,
                            headers,
                            formBody,
                            queryParameter
                        )
                    }
                }
            }
            HttpRequestMethods.DELETE -> return retrofitClient.performDeleteRequest(
                requestUrl,
                headers,
                queryParameter
            )

        }
    }

    private companion object {
        const val BASE_URL_EXCEPTION_MESSAGE =
            "Network request build error - no url path defined!"
        const val EMPTY_REQUEST_EXCEPTION_MESSAGE = "POST request without any body?!"
        val JSON = "application/json".toMediaTypeOrNull()
    }
}
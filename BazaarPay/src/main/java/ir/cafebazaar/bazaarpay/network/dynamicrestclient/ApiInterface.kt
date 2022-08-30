package ir.cafebazaar.bazaarpay.network.dynamicrestclient

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
internal interface ApiInterface {

    @GET
    fun performGetRequest(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @GET
    fun performGetRequestWithParam(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @QueryMap requestParameter: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @POST
    fun performPostRequest(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @Body requestModel: RequestBody,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @FormUrlEncoded
    @POST
    fun performPostRequest(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @FieldMap(encoded = true) parameters: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @FormUrlEncoded
    @POST
    fun performPostRequestNotEncoded(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @FieldMap parameters: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @PUT
    fun performPutRequest(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @Body requestModel: RequestBody,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @FormUrlEncoded
    @PUT
    fun performPutRequest(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @FieldMap(encoded = true) parameters: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @FormUrlEncoded
    @PUT
    fun performPutRequestNotEncoded(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @FieldMap parameters: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @PATCH
    fun performPatchRequest(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @Body requestModel: RequestBody,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @FormUrlEncoded
    @PATCH
    fun performPatchRequest(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @FieldMap(encoded = true) parameters: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @FormUrlEncoded
    @PATCH
    fun performPatchRequestNotEncoded(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @FieldMap parameters: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>

    @DELETE
    fun performDeleteRequest(
        @Url url: String,
        @HeaderMap headers: Map<String, Any>,
        @QueryMap options: Map<String, Any>
    ): Call<ResponseBody?>
}
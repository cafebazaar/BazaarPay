package ir.cafebazaar.bazaarpay.data.payment.api

import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.request.GetPaymentMethodsRequest
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.response.PaymentMethodsInfoDto
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.response.MerchantInfoDto
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.PayRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.response.PayResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface PaymentService {

    @POST("get-payment-methods/")
    suspend fun getPaymentMethods(
        @Body getPaymentMethodsRequest: GetPaymentMethodsRequest,
        @Query(PAY_ENDPOINT_LANG) lang: String
    ): PaymentMethodsInfoDto

    @GET("merchant-info")
    suspend fun getMerchantInfo(
        @Query(CHECKOUT_TOKEN_LABEL) checkoutLabel: String,
        @Query(PAY_ENDPOINT_LANG) lang: String
    ): MerchantInfoDto

    @POST("pay/")
    suspend fun pay(
        @Body payRequest: PayRequest,
        @Query(PAY_ENDPOINT_LANG) lang: String
    ): PayResponse

    companion object {
        const val PAY_ENDPOINT_LANG = "lang"
        const val CHECKOUT_TOKEN_LABEL = "checkout_token"
    }
}
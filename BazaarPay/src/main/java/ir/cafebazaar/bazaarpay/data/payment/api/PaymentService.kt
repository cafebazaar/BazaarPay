package ir.cafebazaar.bazaarpay.data.payment.api

import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.request.GetPaymentMethodsRequest
import ir.cafebazaar.bazaarpay.data.payment.models.getpaymentmethods.response.PaymentMethodsInfoDto
import ir.cafebazaar.bazaarpay.data.payment.models.increasebalance.IncreaseBalanceRequest
import ir.cafebazaar.bazaarpay.data.payment.models.merchantinfo.response.MerchantInfoDto
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.CommitRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.InitCheckoutRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.PayRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.request.TraceRequest
import ir.cafebazaar.bazaarpay.data.payment.models.pay.response.BalanceResponseDto
import ir.cafebazaar.bazaarpay.data.payment.models.pay.response.IncreaseBalanceOptionsResponseDto
import ir.cafebazaar.bazaarpay.data.payment.models.pay.response.InitCheckoutResponse
import ir.cafebazaar.bazaarpay.data.payment.models.pay.response.PayResponse
import ir.cafebazaar.bazaarpay.data.payment.models.pay.response.TraceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface PaymentService {

    @POST("pardakht/badje/v1/get-payment-methods/")
    suspend fun getPaymentMethods(
        @Body getPaymentMethodsRequest: GetPaymentMethodsRequest,
        @Query(PAY_ENDPOINT_LANG) lang: String,
    ): PaymentMethodsInfoDto

    @GET("pardakht/badje/v1/merchant-info")
    suspend fun getMerchantInfo(
        @Query(CHECKOUT_TOKEN_LABEL) checkoutLabel: String,
        @Query(PAY_ENDPOINT_LANG) lang: String
    ): MerchantInfoDto

    @POST("pardakht/badje/v1/pay/")
    suspend fun pay(
        @Body payRequest: PayRequest,
        @Query(PAY_ENDPOINT_LANG) lang: String,
        @Query(PAY_ENDPOINT_ACCESSIBILITY) accessibility: Boolean,
    ): PayResponse

    @POST("pardakht/badje/v1/increase-balance/")
    suspend fun increaseBalance(
        @Body increaseBalanceRequest: IncreaseBalanceRequest
    ): PayResponse

    @POST("pardakht/badje/v1/commit/")
    suspend fun commit(
        @Body commitRequest: CommitRequest
    ): Response<Unit>

    @POST("pardakht/badje/v1/trace/")
    suspend fun trace(
        @Body traceRequest: TraceRequest
    ): TraceResponse

    @POST("pardakht/badje/v1/checkout/init/")
    suspend fun initCheckout(
        @Body initCheckoutRequest: InitCheckoutRequest
    ): InitCheckoutResponse

    @GET("pardakht/badje/v1/get-balance/")
    suspend fun getBalance(): BalanceResponseDto

    @GET("pardakht/badje/v1/get-increase-balance-options/")
    suspend fun getIncreaseBalanceOptions(
        @Query(PAY_ENDPOINT_ACCESSIBILITY) accessibility: Boolean,
    ): IncreaseBalanceOptionsResponseDto

    companion object {

        const val PAY_ENDPOINT_LANG = "lang"
        const val PAY_ENDPOINT_ACCESSIBILITY = "accessibility"
        const val CHECKOUT_TOKEN_LABEL = "checkout_token"
    }
}
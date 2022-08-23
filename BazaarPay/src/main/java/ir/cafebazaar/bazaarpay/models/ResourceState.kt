package ir.cafebazaar.bazaarpay.models

import java.io.Serializable

internal sealed class ResourceState : Serializable {

    object UnKnown : ResourceState()
    object Success : ResourceState()
    object Loading : ResourceState()
    object Error : ResourceState()

    abstract class CustomState : ResourceState()
}

internal sealed class PaymentFlowState : ResourceState.CustomState() {
    object MerchantInfo : PaymentFlowState()
    object PaymentMethodsInfo : PaymentFlowState()
    object DirectDebitObBoardingDetails : PaymentFlowState()
}

internal sealed class VerificationState : ResourceState.CustomState() {
    object Tick : VerificationState()
    object FinishCountDown : VerificationState()
}
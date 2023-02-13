package ir.cafebazaar.bazaarpay

import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel

internal interface FinishCallbacks {

    fun onSuccess()
    fun onFailure(errorModel: ErrorModel?)
    fun onCanceled()
}
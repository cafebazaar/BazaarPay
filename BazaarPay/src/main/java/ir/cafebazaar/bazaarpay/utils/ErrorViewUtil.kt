package ir.cafebazaar.bazaarpay.utils

import android.content.Context
import android.view.View
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.extensions.isNetworkAvailable
import ir.cafebazaar.bazaarpay.widget.errorview.GeneralErrorView
import ir.cafebazaar.bazaarpay.widget.errorview.NetworkErrorView
import ir.cafebazaar.bazaarpay.widget.errorview.NotFoundErrorView
import ir.cafebazaar.bazaarpay.widget.errorview.NotLoginErrorView

fun getErrorViewBasedOnErrorModel(
    context: Context,
    errorModel: ErrorModel,
    onRetryClicked: () -> Unit,
    onLoginClicked: () -> Unit
): View {
    return when (errorModel) {
        is ErrorModel.NotFound,
        is ErrorModel.Forbidden -> {
            getNotFoundErrorView(context, errorModel)
        }
        is ErrorModel.NetworkConnection -> {
            if (context.isNetworkAvailable()) {
                getGeneralErrorView(context, onRetryClicked)
            } else {
                getNetworkErrorView(context, onRetryClicked)
            }
        }
        is ErrorModel.LoginIsRequired -> {
            getLoginErrorView(context, onLoginClicked)
        }
        else -> {
            getGeneralErrorView(context, onRetryClicked)
        }
    }
}

fun getGeneralErrorView(
    context: Context,
    onRetryClicked: () -> Unit
): GeneralErrorView {
    return GeneralErrorView(context).apply {
        setOnRetryButtonClickListener {
            onRetryClicked.invoke()
        }
    }
}

fun getNetworkErrorView(
    context: Context,
    onRetryClicked: () -> Unit
): NetworkErrorView {
    return NetworkErrorView(context).apply {
        setOnRetryButtonClickListener {
            onRetryClicked.invoke()
        }
    }
}

fun getLoginErrorView(
    context: Context,
    onLoginClicked: () -> Unit
): NotLoginErrorView {
    return NotLoginErrorView(context).apply {
        setOnLoginButtonClickListener {
            onLoginClicked.invoke()
        }
    }
}

fun getNotFoundErrorView(
    context: Context,
    errorModel: ErrorModel
): NotFoundErrorView {
    return NotFoundErrorView(context).apply {
        setMessageText(errorModel.message)
    }
}
package ir.cafebazaar.bazaarpay.models

import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import java.io.Serializable

internal data class Resource<out T>(
    val resourceState: ResourceState,
    val data: T? = null,
    val failure: ErrorModel? = null
) : Serializable {

    val isSuccess = resourceState is ResourceState.Success

    val isLoading = resourceState is ResourceState.Loading

    val isEmpty = resourceState is ResourceState.Success && data == null

    val isError = resourceState is ResourceState.Error

    fun getError(): Throwable = failure ?: Throwable("null")

    companion object {

        fun <T> unKnown(
            data: T? = null,
            failure: ErrorModel? = null
        ) = Resource<T>(ResourceState.UnKnown, data, failure)

        fun <T> loaded(
            data: T? = null,
            failure: ErrorModel? = null
        ) = Resource<T>(ResourceState.Success, data, failure)

        fun <T> loading(
            data: T? = null,
            failure: ErrorModel? = null
        ) = Resource<T>(ResourceState.Loading, data, failure)

        fun <T> failed(
            data: T? = null,
            failure: ErrorModel? = null
        ) = Resource<T>(ResourceState.Error, data, failure)
    }
}
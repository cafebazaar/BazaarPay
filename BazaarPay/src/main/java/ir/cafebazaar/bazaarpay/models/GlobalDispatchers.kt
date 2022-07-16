package ir.cafebazaar.bazaarpay.models

import kotlinx.coroutines.CoroutineDispatcher

internal data class GlobalDispatchers(
    val main: CoroutineDispatcher,
    val iO: CoroutineDispatcher,
    val default: CoroutineDispatcher
)
package ir.cafebazaar.bazaarpay.network.gson.wrapper

import ir.cafebazaar.bazaarpay.network.gson.SweepWrapper
import ir.cafebazaar.bazaarpay.network.gson.USE_DEFAULT_WRAPPER

/**
 * Indicates that [SweepWrapper] must use [wrapWith] method to find out the wrapper names,
 * when the [SweepWrapper.value] is set to [USE_DEFAULT_WRAPPER].
 */
interface DefaultWrapper {
    fun <T> wrapWith(value: T): String?
}
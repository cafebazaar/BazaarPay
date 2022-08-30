package ir.cafebazaar.bazaarpay.network.gson.wrapper

import ir.cafebazaar.bazaarpay.network.gson.SweepWrapper
import ir.cafebazaar.bazaarpay.network.gson.USE_CLASS_NAME_WRAPPER
import ir.cafebazaar.bazaarpay.network.gson.USE_DEFAULT_WRAPPER
import ir.cafebazaar.bazaarpay.network.model.SweepReflection
import ir.cafebazaar.bazaarpay.network.model.SweepReflection.isAnnotationPresent

/**
 * Builds a list of wrapper names, which will be used on [WrapperTypeAdapter], based on [SweepWrapper.value] or [DefaultWrapper.wrapWith].
 */
internal class WrapperNamesBuilder(private val defaultWrapper: DefaultWrapper) {

    fun <T> build(value: T): List<String> {
        if (!isAnnotationPresent(value, SweepWrapper::class.java)) {
            throw IllegalStateException("Class $value must be annotation with SweepWrapper.")
        }

        var names = findWrapperNamesFromAnnotation(value)
        names = applyDefaultWrapper(value, names)
        names = applyClassNameWrapper(value, names)

        return names
    }

    private fun <T> findWrapperNamesFromAnnotation(value: T): List<String> {
        return SweepReflection.sweepWrapperValue(value).trim().split(DOT_SPLITTER)
    }

    private fun <T> applyDefaultWrapper(value: T, oldNames: List<String>): List<String> {
        val newNames = mutableListOf<String>()

        oldNames.forEach {
            when (it) {
                USE_DEFAULT_WRAPPER -> {
                    val defaultWrappers = defaultWrapper.wrapWith(value)
                        ?.trim()
                        ?.split(DOT_SPLITTER)

                        ?: throw IllegalStateException(
                            "$value forced to use default wrapper, but nothing provided." +
                                " Try to implement DefaultWrapper"
                        )
                    newNames.addAll(defaultWrappers)
                }
                else -> {
                    newNames.add(it)
                }
            }
        }
        return newNames
    }

    private fun <T> applyClassNameWrapper(value: T, names: List<String>): List<String> {
        return names.map {
            when (it) {
                USE_CLASS_NAME_WRAPPER -> {
                    SweepReflection.findClassName(value).decapitalize()
                }
                else -> {
                    it
                }
            }
        }
    }

    private companion object {

        const val DOT_SPLITTER = "."
    }
}
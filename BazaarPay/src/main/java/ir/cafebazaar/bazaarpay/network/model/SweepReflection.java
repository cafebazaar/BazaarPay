package ir.cafebazaar.bazaarpay.network.model;

import java.lang.annotation.Annotation;

import ir.cafebazaar.bazaarpay.network.gson.SweepUnwrapper;
import ir.cafebazaar.bazaarpay.network.gson.SweepWrapper;

public class SweepReflection {

    public static <T, R extends Annotation> boolean isAnnotationPresent(T value, Class<R> annotation) {
        return value.getClass().isAnnotationPresent(annotation);
    }

    public static <T, R extends Annotation> boolean isAnnotationPresent(Class<T> value, Class<R> annotation) {
        return value.isAnnotationPresent(annotation);
    }

    public static <T> String sweepWrapperValue(T value) {
        return value.getClass().getAnnotation(SweepWrapper.class).value();
    }

    public static <T> String sweepUnwrapperValue(Class<T> value) {
        return value.getAnnotation(SweepUnwrapper.class).value();
    }

    public static <T> String findClassName(T value) {
        return value.getClass().getSimpleName();
    }

    public static <T> String findClassName(Class<T> value) {
        return value.getSimpleName();
    }
}
package ir.cafebazaar.bazaarpay.analytics.model

internal enum class EventType {
    UNKNOWN_TYPE,
    CLICK,
    SWIPE,
    PROCESS,
    LOAD,
    CLOSE,
    VISIT,
    CHANGE_FOCUS,
    ERROR;
}
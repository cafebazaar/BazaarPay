package ir.cafebazaar.bazaarpay.utils

import java.util.concurrent.TimeUnit

internal class StopWatch {

    private var lastStartTimeInNanos: Long = ZERO_TIME
    private var totalTimeNanos: Long = ZERO_TIME

    fun start() {
        if (lastStartTimeInNanos != ZERO_TIME) {
            return
        }
        lastStartTimeInNanos = System.nanoTime()
        totalTimeNanos = ZERO_TIME
    }

    fun stop() {
        if (lastStartTimeInNanos != ZERO_TIME) {
            calculateTotalTime()
        }
    }

    fun pause() {
        totalTimeNanos += System.nanoTime() - lastStartTimeInNanos
        lastStartTimeInNanos = ZERO_TIME
    }

    fun isPaused(): Boolean {
        return lastStartTimeInNanos == ZERO_TIME
    }

    private fun calculateTotalTime() {
        totalTimeNanos += System.nanoTime() - lastStartTimeInNanos
        lastStartTimeInNanos = ZERO_TIME
    }

    fun getElapsedTimeMillis(): Long {
        return TimeUnit.NANOSECONDS.toMillis(totalTimeNanos)
    }

    companion object {

        private const val ZERO_TIME = 0L
    }
}
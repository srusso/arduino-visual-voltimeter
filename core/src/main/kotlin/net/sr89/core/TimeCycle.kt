package net.sr89.core

import java.time.Duration

class TimeCycle(private val duration: Duration) {

    var start: Long = 0

    fun startCycle() {
        start = System.nanoTime()
    }

    fun currentPositionWithinCycle(currentNanos: Long): Float {
        val nanosElapsedSinceCycleStart = currentNanos - start
        val currentCycle = currentCycle(currentNanos)

        val nanosInCurrentCycle = nanosElapsedSinceCycleStart - (currentCycle * duration.toNanos())

        return nanosInCurrentCycle.toFloat() / duration.toNanos().toFloat()
    }

    fun currentCycle(currentNanos: Long): Long {
        val nanosElapsedSinceCycleStart = currentNanos - start

        return nanosElapsedSinceCycleStart / duration.toNanos()
    }
}
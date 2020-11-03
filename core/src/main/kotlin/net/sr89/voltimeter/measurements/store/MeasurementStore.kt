package net.sr89.voltimeter.measurements.store

import net.sr89.voltimeter.measurements.Measurement

interface MeasurementStore {
    fun add(measurement: Measurement)

    fun get(i: Int): Measurement

    fun size(): Int

    fun min(): Measurement?

    fun max(): Measurement?

    fun oldest(): Measurement?

    fun copyForRenderThread(): MeasurementStore
}
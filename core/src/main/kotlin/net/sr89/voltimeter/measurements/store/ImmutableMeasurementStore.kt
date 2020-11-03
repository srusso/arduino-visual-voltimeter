package net.sr89.voltimeter.measurements.store

import net.sr89.voltimeter.measurements.Measurement

class ImmutableMeasurementStore(
        private val measurements: Array<Measurement?>,
        private val size: Int,
        private val min: Measurement?,
        private val max: Measurement?,
        private val oldest: Measurement?
) : MeasurementStore {

    override fun add(measurement: Measurement) {
        throw UnsupportedOperationException("Add operation not supported by " + javaClass.simpleName)
    }

    override fun get(i: Int): Measurement {
        return measurements[i]!!
    }

    override fun size(): Int {
        return size
    }

    override fun min(): Measurement? {
        return min
    }

    override fun max(): Measurement? {
        return max
    }

    override fun oldest(): Measurement? {
        return oldest
    }

    override fun copyForRenderThread(): MeasurementStore {
        return this
    }

}
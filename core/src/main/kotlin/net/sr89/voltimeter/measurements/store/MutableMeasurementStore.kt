package net.sr89.voltimeter.measurements.store

import com.badlogic.gdx.utils.Queue
import net.sr89.voltimeter.measurements.Measurement
import java.time.Duration
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

class MutableMeasurementStore(private val maxMeasurementCount: Int, private val maxMeasurementInterval: Duration) : MeasurementStore {
    private val measurements: Queue<Measurement> = Queue(maxMeasurementCount + 1)
    private val measurementArrayForCopy: Array<Measurement?> = Array(maxMeasurementCount) { null }

    private var minMeasurement: Measurement? = null
    private var maxMeasurement: Measurement? = null

    /**
     * Lock to be used around accesses to [measurements].
     * Locking isn't needed for the other two variables; in the worst case scenario,
     * we will return a slightly out of date value which will be updated before the next frame.
     */
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    override fun add(measurement: Measurement) {
        lock.writeLock().withLock {
            measurements.addLast(measurement)

            if (measurements.size > maxMeasurementCount) {
                measurements.removeFirst()
            }
        }

        if (minMeasurement == null || measurement.voltage < minMeasurement!!.voltage) {
            minMeasurement = measurement
        }

        if (maxMeasurement == null || measurement.voltage > maxMeasurement!!.voltage) {
            maxMeasurement = measurement
        }
    }

    override fun get(i: Int): Measurement {
        return lock.readLock().withLock { measurements.get(i) }
    }

    override fun size(): Int {
        return lock.readLock().withLock { kotlin.math.min(measurements.size, maxMeasurementCount) }
    }

    override fun min(): Measurement? {
        return minMeasurement
    }

    override fun max(): Measurement? {
        return maxMeasurement
    }

    override fun copyForRenderThread(): MeasurementStore {
        return lock.readLock().withLock {
            for(i in 0 until measurements.size) {
                measurementArrayForCopy[i] = measurements.get(i)
            }

            for(i in measurements.size until measurementArrayForCopy.size) {
                measurementArrayForCopy[i] = null
            }

            ImmutableMeasurementStore(measurementArrayForCopy, measurements.size, minMeasurement, maxMeasurement)
        }
    }
}
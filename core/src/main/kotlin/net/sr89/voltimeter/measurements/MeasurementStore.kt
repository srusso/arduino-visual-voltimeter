package net.sr89.voltimeter.measurements

import com.badlogic.gdx.utils.Queue
import java.time.Duration

class MeasurementStore(private val maxMeasurementCount: Int, private val maxMeasurementInterval: Duration) {
    private val measurements: Queue<Measurement> = Queue(maxMeasurementCount + 1)

    private var minMeasurement: Measurement? = null
    private var maxMeasurement: Measurement? = null

    fun add(measurement: Measurement) {
        measurements.addLast(measurement)

        if (measurements.size > maxMeasurementCount) {
            measurements.removeFirst()
        }

        if (minMeasurement == null || measurement.voltage < minMeasurement!!.voltage) {
            minMeasurement = measurement
        }

        if (maxMeasurement == null || measurement.voltage > maxMeasurement!!.voltage) {
            maxMeasurement = measurement
        }
    }

    fun get(i: Int): Measurement {
        return measurements.get(i)
    }

    fun size(): Int {
        return kotlin.math.min(measurements.size, maxMeasurementCount)
    }

    fun min(): Measurement? {
        return minMeasurement
    }

    fun max(): Measurement? {
        return maxMeasurement
    }

    fun iterate(): Iterator<Measurement> {
        return measurements.iterator()
    }
}
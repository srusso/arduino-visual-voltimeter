package net.sr89.voltimeter.measurements

import java.time.Instant
import kotlin.random.Random

class MeasurementSource {
    private val random: Random = Random(2)

    fun nextMeasurement(): Measurement {
        return Measurement(random.nextFloat() * 5, Instant.now())
    }
}
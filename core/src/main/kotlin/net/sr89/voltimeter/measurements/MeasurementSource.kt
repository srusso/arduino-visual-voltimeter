package net.sr89.voltimeter.measurements

import com.badlogic.gdx.utils.TimeUtils
import kotlin.random.Random

class MeasurementSource {
    private val random: Random = Random(2)

    fun nextMeasurement(): Measurement {
        return Measurement(random.nextFloat() * 5, TimeUtils.millis())
    }
}
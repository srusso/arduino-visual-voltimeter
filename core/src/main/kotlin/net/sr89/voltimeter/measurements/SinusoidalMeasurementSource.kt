package net.sr89.voltimeter.measurements

import com.badlogic.gdx.utils.TimeUtils
import kotlin.math.sin

class SinusoidalMeasurementSource: MeasurementSource {
    private var a: Double = 0.0

    override fun nextMeasurement(): Measurement {
        a += 0.1
        val voltage = sin(2 * a).toFloat() * 5
        return Measurement(voltage, TimeUtils.millis())
    }

    override fun close() {
        // nothing to do
    }
}
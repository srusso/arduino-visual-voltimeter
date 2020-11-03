package net.sr89.voltimeter.measurements

import java.io.Closeable

interface MeasurementSource : Closeable {
    fun nextMeasurement(): Measurement?
}
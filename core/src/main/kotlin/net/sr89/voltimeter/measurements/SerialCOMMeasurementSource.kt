package net.sr89.voltimeter.measurements

import com.badlogic.gdx.utils.TimeUtils
import com.fazecast.jSerialComm.SerialPort
import net.sr89.voltimeter.util.math.missingRatioValue
import java.time.Duration
import net.sr89.arduino.serial.read.direct.BlockingReader as Arduino

class SerialCOMMeasurementSource : MeasurementSource {
    private val port: SerialPort = SerialPort.getCommPort("COM3")
    private val arduino: Arduino = Arduino(port, Duration.ofMillis(100))

    override fun nextMeasurement(): Measurement? {
        val readResult = arduino.tryRead(1)

        return readResult
                .map { data -> Measurement(toVoltage(data.data), TimeUtils.millis()) }
                .orElse(null)
    }

    private fun toVoltage(data: ByteArray): Float {
        return missingRatioValue((2 * data[0]).toFloat(), 255F, 5F) - 5F
    }

    override fun close() {
        arduino.close()
    }
}
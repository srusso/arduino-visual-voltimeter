package net.sr89.voltimeter.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import net.sr89.voltimeter.input.MouseInputProcessor
import net.sr89.voltimeter.measurements.MeasurementStore
import net.sr89.voltimeter.util.math.missingRatioValue
import java.time.Duration
import kotlin.math.max
import kotlin.math.min

class MeasurementRenderer {
    private val timeMemory = Duration.ofSeconds(5)

    fun render(measurementStore: MeasurementStore, mouseInputProcessor: MouseInputProcessor, shapeRenderer: ShapeRenderer) {
        if (measurementStore.size() < 10) {
            return
        }

        val width = Gdx.graphics.width
        val height = Gdx.graphics.height
        val startX: Float = (width / 5).toFloat()
        val endX: Float = 4 * startX
        val startY: Float = (height / 5).toFloat()
        val endY: Float = 4 * startY

        val floatArray = measurementsToCoordinates(measurementStore, startX, endX, startY, endY)

        shapeRenderer.polyline(floatArray)
        shapeRenderer.line(Vector2(startX, startY), Vector2(startX, endY))
        shapeRenderer.line(Vector2(startX, startY), Vector2(endX, startY))
        renderInfoLine(shapeRenderer, mouseInputProcessor, startY, endY, startX, endX)
    }

    private fun renderInfoLine(
            shapeRenderer: ShapeRenderer,
            mouseInputProcessor: MouseInputProcessor,
            startY: Float,
            endY: Float,
            startX: Float,
            endX: Float) {
        val xPosition = max(min(mouseInputProcessor.currentMouseX().toFloat(), endX), startX)

        val oldColor = shapeRenderer.color

        shapeRenderer.color = Color.RED
        shapeRenderer.line(Vector2(xPosition, startY), Vector2(xPosition, endY))

        shapeRenderer.color = oldColor
    }

    private fun measurementsToCoordinates(
            measurementStore: MeasurementStore,
            startX: Float,
            endX: Float,
            startY: Float,
            endY: Float
    ): FloatArray {
        val floatArray = FloatArray(measurementStore.size() * 2)

        val now = TimeUtils.millis()
        val minTimestamp = now - timeMemory.toMillis()
        val timeMemoryMillis = timeMemory.toMillis().toFloat()
        val xBoxSize = endX - startX
        val yBoxSize = endY - startY
        val minMeasurement = measurementStore.min()!!
        val maxMeasurement = measurementStore.max()!!
        val measurementDelta = maxMeasurement.voltage - minMeasurement.voltage

        for (i in 0 until measurementStore.size()) {
            val measurement = measurementStore.get(i)
            val timeFromOrigin = max(measurement.timestamp - minTimestamp, 0)

            // x coordinate (time)
            floatArray[2 * i] = startX + missingRatioValue(timeFromOrigin.toFloat(), timeMemoryMillis, xBoxSize)

            // y coordinate (measurement)
            floatArray[2 * i + 1] = startY + missingRatioValue(measurement.voltage - minMeasurement.voltage, measurementDelta, yBoxSize)
        }
        return floatArray
    }
}
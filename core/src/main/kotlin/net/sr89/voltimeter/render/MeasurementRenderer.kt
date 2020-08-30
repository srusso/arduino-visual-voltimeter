package net.sr89.voltimeter.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import net.sr89.voltimeter.input.MouseInputProcessor
import net.sr89.voltimeter.measurements.Measurement
import net.sr89.voltimeter.measurements.MeasurementStore
import net.sr89.voltimeter.util.math.missingRatioValue
import java.time.Duration
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MeasurementRenderer {
    private val timeMemory = Duration.ofSeconds(5)

    fun render(measurementStore: MeasurementStore, mouseInputProcessor: MouseInputProcessor, shapeRenderer: ShapeRenderer, spriteBatch: SpriteBatch, font: BitmapFont) {
        if (measurementStore.size() < 10) {
            return
        }

        val width = Gdx.graphics.width
        val height = Gdx.graphics.height
        val startX: Float = (width / 5).toFloat()
        val endX: Float = 4 * startX
        val startY: Float = (height / 5).toFloat()
        val endY: Float = 4 * startY

        val coordinates = measurementsToCoordinates(measurementStore, mouseInputProcessor, startX, endX, startY, endY)

        shapeRenderer.polyline(coordinates.first)
        shapeRenderer.line(Vector2(startX, startY), Vector2(startX, endY))
        shapeRenderer.line(Vector2(startX, startY), Vector2(endX, startY))
        renderInfoLine(shapeRenderer, mouseInputProcessor, startY, endY, startX, endX)
        printInfoLineMeasurement(spriteBatch, font, startX, startY, coordinates.second)
    }

    private fun printInfoLineMeasurement(spriteBatch: SpriteBatch, font: BitmapFont, startX: Float, startY: Float, measurement: Measurement) {
        spriteBatch.begin()
        font.draw(spriteBatch, String.format("%.2f", measurement.voltage), startX, startY - 10)
        spriteBatch.end()
    }

    private fun renderInfoLine(
            shapeRenderer: ShapeRenderer,
            mouseInputProcessor: MouseInputProcessor,
            startY: Float,
            endY: Float,
            startX: Float,
            endX: Float) {
        val xPosition = actualInfoLineXPosition(mouseInputProcessor, endX, startX)

        val oldColor = shapeRenderer.color

        shapeRenderer.color = Color.RED
        shapeRenderer.line(Vector2(xPosition, startY), Vector2(xPosition, endY))

        shapeRenderer.color = oldColor
    }

    /**
     * Returns the array of coordinates to be rendered for the measurements,
     * and also measurement closer to the vertical info line
     */
    private fun measurementsToCoordinates(
            measurementStore: MeasurementStore,
            mouseInputProcessor: MouseInputProcessor,
            startX: Float,
            endX: Float,
            startY: Float,
            endY: Float
    ): Pair<FloatArray, Measurement> {
        val floatArray = FloatArray(measurementStore.size() * 2)

        val now = TimeUtils.millis()
        val minTimestamp = now - timeMemory.toMillis()
        val timeMemoryMillis = timeMemory.toMillis().toFloat()
        val xBoxSize = endX - startX
        val yBoxSize = endY - startY
        val minMeasurement = measurementStore.min()!!
        val maxMeasurement = measurementStore.max()!!
        val measurementDelta = maxMeasurement.voltage - minMeasurement.voltage
        val infoLineXPosition = actualInfoLineXPosition(mouseInputProcessor, endX, startX)
        var closestMeasurement: Measurement = measurementStore.get(0)
        val minDistanceFromInfoLine: Float = Float.POSITIVE_INFINITY

        for (i in 0 until measurementStore.size()) {
            val measurement = measurementStore.get(i)
            val timeFromOrigin = max(measurement.timestamp - minTimestamp, 0)

            // x coordinate (time)
            val xPosition = startX + missingRatioValue(timeFromOrigin.toFloat(), timeMemoryMillis, xBoxSize)
            floatArray[2 * i] = xPosition
            if (abs(infoLineXPosition - xPosition) < minDistanceFromInfoLine) {
                closestMeasurement = measurement
            }

            // y coordinate (measurement)
            floatArray[2 * i + 1] = startY + missingRatioValue(measurement.voltage - minMeasurement.voltage, measurementDelta, yBoxSize)
        }

        return Pair(floatArray, closestMeasurement)
    }

    private fun actualInfoLineXPosition(mouseInputProcessor: MouseInputProcessor, endX: Float, startX: Float) =
            max(min(mouseInputProcessor.currentMouseX().toFloat(), endX), startX)
}
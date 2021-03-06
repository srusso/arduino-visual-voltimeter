package net.sr89.voltimeter.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import net.sr89.voltimeter.collection.CircularQueue
import net.sr89.voltimeter.input.MouseInputProcessor
import net.sr89.voltimeter.measurements.Measurement
import net.sr89.voltimeter.measurements.store.MeasurementStore
import net.sr89.voltimeter.util.math.missingRatioValue
import java.time.Duration
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class MeasurementRenderer {
    private fun CircularQueue<Long>.averageSuccessiveDifference(): Float {
        if (size() < 2) {
            return 0F
        }

        var totalDifference = 0L

        for(i in 1 until size()) {
            totalDifference += get(i) - get(i - 1)
        }

        return totalDifference.toFloat() / size()
    }

    private fun <T: Number> CircularQueue<T>.roundedAverage(): Long {
        if (size() == 0) {
            return 0
        }

        var total: Long = 0

        for(i in 0 until size()) {
            total += get(i).toLong()
        }

        return total / size()
    }

    private val timeMemory = Duration.ofSeconds(5)
    private var renderedMeasurements: Int = 0
    private var renderingTimestamps: CircularQueue<Long> = CircularQueue(10)
    private var fpsValues: CircularQueue<Int> = CircularQueue(100)
    private var mpsValues: CircularQueue<Float> = CircularQueue(100)

    fun render(measurementStore: MeasurementStore, mouseInputProcessor: MouseInputProcessor, shapeRenderer: ShapeRenderer, spriteBatch: SpriteBatch, font: BitmapFont) {
        if (measurementStore.size() < 10) {
            return
        }

        renderingTimestamps.add(TimeUtils.millis())

        val width = Gdx.graphics.width
        val height = Gdx.graphics.height
        val startX: Float = (width / 5).toFloat()
        val endX: Float = 4 * startX
        val startY: Float = (height / 5).toFloat()
        val endY: Float = 4 * startY
        val midY: Float = startY + ((endY - startY) / 2)
        val yBoxSize: Float = endY - startY
        val xBoxSize: Float = endX - startX
        val dottedLineYDistance: Float = yBoxSize / 6

        val coordinates = measurementsToCoordinates(measurementStore, mouseInputProcessor, startX, endX, startY, endY)

        shapeRenderer.color = Color.GREEN

        renderHorizontalDottedLines(measurementStore, spriteBatch, font, startX, startY, endX, midY, dottedLineYDistance, shapeRenderer)

        shapeRenderer.line(Vector2(startX, midY), Vector2(endX, midY))
        
        shapeRenderer.color = Color.GREEN
        shapeRenderer.line(Vector2(startX, startY), Vector2(startX, endY))

        shapeRenderer.color = Color.RED
        shapeRenderer.polyline(coordinates.first)

        shapeRenderer.color = Color.WHITE
        renderInfoLine(shapeRenderer, mouseInputProcessor, startY, endY, startX, endX)
        printStats(spriteBatch, font, startX, startY, coordinates.second)
    }

    private fun renderHorizontalDottedLines(
            measurementStore: MeasurementStore, spriteBatch: SpriteBatch, font: BitmapFont, startX: Float, startY: Float, endX: Float, midY: Float, dottedLineYDistance: Float, shapeRenderer: ShapeRenderer) {
        val dottedLineLength = 5

        for (lineXStart in generateSequence(startX) { currX -> currX + dottedLineLength * 2 }.takeWhile { currX -> currX + dottedLineLength < endX }) {
            for (mult in sequenceOf(-3, -2, -1, 1, 2, 3)) {
                val lineY = midY + dottedLineYDistance * mult
                shapeRenderer.line(Vector2(lineXStart, lineY), Vector2(lineXStart + dottedLineLength, lineY))

                val measurementRange = (measurementStore.max()?.voltage ?: 0F) - (measurementStore.min()?.voltage ?: 0F)
                val lineMeasurementIndicator = measurementRange * (mult.toFloat() / 6F)
                spriteBatch.begin()
                font.draw(spriteBatch, String.format("%.2f", lineMeasurementIndicator), startX - 50, lineY)
                spriteBatch.end()
            }
        }
    }

    private fun printStats(
            spriteBatch: SpriteBatch,
            font: BitmapFont, startX: Float,
            startY: Float,
            whiteLineMeasurement: Measurement) {
        spriteBatch.begin()
        val infoBoxText = String.format(
                "Value at white line: %.2f\n" +
                        "Measurements rendered: %d\n" +
                        "Measurements per second: %d\n" +
                        "Rendering FPS: %d\n",
                whiteLineMeasurement.voltage,
                renderedMeasurements,
                measurementsPerSecond(),
                getFps()
        )
        font.draw(spriteBatch, infoBoxText, startX, startY - 10)
        spriteBatch.end()
    }

    private fun measurementsPerSecond(): Long {
        val mpsValue = renderedMeasurements.toFloat() / timeMemory.toSeconds()
        mpsValues.add(mpsValue)
        return mpsValues.roundedAverage()
    }

    private fun getFps(): Long {
        val currentFps = (Duration.ofSeconds(1).toMillis().toFloat() / renderingTimestamps.averageSuccessiveDifference()).roundToInt()
        fpsValues.add(currentFps)
        return fpsValues.roundedAverage()
    }

    private fun renderInfoLine(
            shapeRenderer: ShapeRenderer,
            mouseInputProcessor: MouseInputProcessor,
            startY: Float,
            endY: Float,
            startX: Float,
            endX: Float) {
        val xPosition = actualInfoLineXPosition(mouseInputProcessor, endX, startX)

        shapeRenderer.line(Vector2(xPosition, startY), Vector2(xPosition, endY))
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
        val measurementCount = measurementStore.size()

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
        var minDistanceFromInfoLine: Float = Float.POSITIVE_INFINITY

        var firstMeasurementWithinMemory = 0

        for (i in 0 until measurementCount) {
            val measurement = measurementStore.get(i)

            if (measurement.timestamp > minTimestamp) {
                firstMeasurementWithinMemory = i
                break
            }
        }

        renderedMeasurements = measurementCount - firstMeasurementWithinMemory
        val floatArray = FloatArray(renderedMeasurements * 2)

        for (i in firstMeasurementWithinMemory until measurementCount) {
            val measurement = measurementStore.get(i)
            val timeFromOrigin = max(measurement.timestamp - minTimestamp, 0)

            // x coordinate (time)
            val xPosition = startX + 1 + missingRatioValue(timeFromOrigin.toFloat(), timeMemoryMillis, xBoxSize)
            val floatArrayIndex = i - firstMeasurementWithinMemory
            floatArray[2 * floatArrayIndex] = xPosition
            if (abs(infoLineXPosition - xPosition) < minDistanceFromInfoLine) {
                minDistanceFromInfoLine = abs(infoLineXPosition - xPosition)
                closestMeasurement = measurement
            }

            // y coordinate (measurement)
            floatArray[2 * floatArrayIndex + 1] = startY + missingRatioValue(measurement.voltage - minMeasurement.voltage, measurementDelta, yBoxSize)
        }

        return Pair(floatArray, closestMeasurement)
    }

    private fun actualInfoLineXPosition(mouseInputProcessor: MouseInputProcessor, endX: Float, startX: Float) =
            max(min(mouseInputProcessor.currentMouseX().toFloat(), endX), startX)
}
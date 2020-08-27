package net.sr89.voltimeter.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import net.sr89.voltimeter.measurements.MeasurementStore
import kotlin.math.max

class MeasurementRenderer {
    fun render(measurementStore: MeasurementStore, shapeRenderer: ShapeRenderer) {
        if (measurementStore.size() < 10) {
            return
        }

        val width = Gdx.graphics.width
        val height = Gdx.graphics.height
        val startX: Float = (width / 5).toFloat()
        val endX: Float = 4 * startX
        val startY: Float = (height / 5).toFloat()
        val endY: Float = 4 * startY

        val floatArray = FloatArray(measurementStore.size() * 2)

        val now = TimeUtils.millis()
        val fiveSecondsAgo = now - 5000

        for(i in 0 until measurementStore.size()) {
            val measurement = measurementStore.get(i)
            val timeDelta = max(measurement.timestamp - fiveSecondsAgo, 0)
            val displacementX: Float = timeDelta / 5000F

            floatArray[2 * i] = startX + displacementX * 50 //x
            floatArray[2 * i + 1] = startY + measurement.voltage * 50 //y
        }

        shapeRenderer.polyline(floatArray)
        shapeRenderer.line(Vector2(startX, startY), Vector2(startX, endY))
        shapeRenderer.line(Vector2(startX, startY), Vector2(endX, startY))
    }
}
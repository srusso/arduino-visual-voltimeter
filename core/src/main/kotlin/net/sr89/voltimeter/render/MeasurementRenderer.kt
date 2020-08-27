package net.sr89.voltimeter.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import net.sr89.voltimeter.measurements.MeasurementStore

class MeasurementRenderer {
    fun render(measurementStore: MeasurementStore, shapeRenderer: ShapeRenderer) {
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height
        val startX: Float = (width / 5).toFloat()
        val endX: Float = 4 * startX
        val startY: Float = (height / 5).toFloat()
        val endY: Float = 4 * startY

        val floatArray = FloatArray(8)
        floatArray[0] = 0F //x0
        floatArray[1] = 0F //y0
        floatArray[2] = 1F //x1
        floatArray[3] = 2.5F //y1
        floatArray[4] = 2F //x2
        floatArray[5] = 2.0F //y2
        floatArray[6] = 3F //x2
        floatArray[7] = 1.3F //y2

        for (i in floatArray.indices) {
            if (i % 2 == 0) {
                floatArray[i] = startX + floatArray[i] * 50
            } else {
                floatArray[i] = startY + floatArray[i] * 50
            }
        }

        shapeRenderer.polyline(floatArray)
        shapeRenderer.line(Vector2(startX, startY), Vector2(startX, endY))
        shapeRenderer.line(Vector2(startX, startY), Vector2(endX, startY))
    }
}
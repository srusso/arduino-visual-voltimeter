package net.sr89.voltimeter.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import net.sr89.voltimeter.core.TimeCycle

class CycleRenderer {
    fun render(cycle: TimeCycle, currentNanos: Long, shapeRenderer: ShapeRenderer): Pair<Float, Float> {
        val currentPositionWithinCycle = cycle.currentPositionWithinCycle(currentNanos)
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height

        val clockCenterX = width.toFloat() / 2
        val clockCenterY = height.toFloat() / 2
        shapeRenderer.arc(
                clockCenterX,
                clockCenterY,
                20F,
                0F,
                360F * currentPositionWithinCycle)

        return Pair(clockCenterX, clockCenterY)
    }
}
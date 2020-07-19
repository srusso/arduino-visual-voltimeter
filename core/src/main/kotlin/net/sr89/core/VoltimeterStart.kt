package net.sr89.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import java.time.Duration

class VoltimeterStart : ApplicationAdapter() {
    private var batch: SpriteBatch? = null
    private var image: Texture? = null
    private var font: BitmapFont? = null

    private var stars: Array<Vector2>? = null
    private var shapeRenderer: ShapeRenderer? = null
    private var lastFrameNanos: Long = 0
    private var lastCycleNanos: Long = 0
    private val cycle = TimeCycle(Duration.ofSeconds(1))

    override fun create() {
        batch = SpriteBatch()
        image = Texture("badlogic.png")
        shapeRenderer = ShapeRenderer()
        font = BitmapFont()
        stars = StarSupplier(0.01f).initStars()
        cycle.startCycle()
    }

    override fun render() {
        val currentNanos = System.nanoTime()
        val currentPositionWithinCycle = cycle.currentPositionWithinCycle(currentNanos)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height
        shapeRenderer!!.begin(ShapeType.Point)
        shapeRenderer!!.setColor(Color.WHITE)
        for (i in 0 until stars!!.size) {
            val star = stars!![i]
            shapeRenderer!!.point(star.x * width, star.y * height, 0f)
        }
        shapeRenderer!!.end()

        shapeRenderer!!.begin(ShapeType.Line)
        shapeRenderer!!.color = Color.YELLOW
        val clockCenterX = width.toFloat() / 2
        val clockCenterY = height.toFloat() / 2
        shapeRenderer!!.arc(
                clockCenterX,
                clockCenterY,
                20F,
                0F,
                360F * currentPositionWithinCycle)

        shapeRenderer!!.end()

        batch!!.begin()
        font!!.draw(batch, cycle.currentCycle(currentNanos).toString(), clockCenterX + 50, clockCenterY + 50)
        batch!!.end()
    }

    override fun dispose() {
        batch!!.dispose()
        image!!.dispose()
        shapeRenderer!!.dispose()
    }
}
package net.sr89.voltimeter.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import net.sr89.voltimeter.input.MouseInputProcessor
import net.sr89.voltimeter.measurements.MeasurementSource
import net.sr89.voltimeter.measurements.MeasurementStore
import net.sr89.voltimeter.render.MeasurementRenderer
import java.time.Duration

class VoltimeterStart : ApplicationAdapter() {
    private var batch: SpriteBatch? = null
    private var image: Texture? = null
    private var font: BitmapFont? = null
    private var measurementThread: Thread? = null
    private var stopMeasuring: Boolean = false

    private var shapeRenderer: ShapeRenderer? = null
    private val cycle = TimeCycle(Duration.ofSeconds(1))
    private val measurementRenderer: MeasurementRenderer = MeasurementRenderer()
    private val measurementStore: MeasurementStore = MeasurementStore(1000, Duration.ofSeconds(10))
    private val measurementSource: MeasurementSource = MeasurementSource()
    private val mouseInputProcessor: MouseInputProcessor = MouseInputProcessor()

    override fun create() {
        batch = SpriteBatch()
        image = Texture("badlogic.png")
        shapeRenderer = ShapeRenderer()
        font = BitmapFont()
        cycle.startCycle()
        Gdx.input.inputProcessor = mouseInputProcessor
        measurementThread = Thread(Runnable {
            while(true) {
                if (stopMeasuring) {
                    println("Bye!")
                    return@Runnable
                }
                measurementStore.add(measurementSource.nextMeasurement())
                Thread.sleep(100)
            }
        })

        measurementThread!!.start()
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        shapeRenderer!!.begin(ShapeType.Line)
        shapeRenderer!!.color = Color.YELLOW

        measurementRenderer.render(measurementStore, mouseInputProcessor, shapeRenderer!!)

        shapeRenderer!!.end()
    }

    private fun drawCycleCount(currentNanos: Long, clockCenter: Pair<Float, Float>) {
        batch!!.begin()
        font!!.draw(batch, cycle.currentCycle(currentNanos).toString(), clockCenter.first + 50, clockCenter.second + 50)
        batch!!.end()
    }

    override fun dispose() {
        batch!!.dispose()
        image!!.dispose()
        shapeRenderer!!.dispose()
        stopMeasuring = true
    }
}
package net.sr89.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

class VoltimeterStart : ApplicationAdapter() {
    private var batch: SpriteBatch? = null
    private var image: Texture? = null
    private var stars: Array<Vector2>? = null
    private var shapeRenderer: ShapeRenderer? = null

    override fun create() {
        batch = SpriteBatch()
        image = Texture("badlogic.png")
        shapeRenderer = ShapeRenderer()
        stars = StarSupplier(0.01f).initStars()
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height
        shapeRenderer!!.begin(ShapeRenderer.ShapeType.Point)
        for (i in 0 until stars!!.size) {
            val star = stars!![i]
            shapeRenderer!!.point(star.x * width, star.y * height, 0f)
        }
        shapeRenderer!!.end()
    }

    override fun dispose() {
        batch!!.dispose()
        image!!.dispose()
        shapeRenderer!!.dispose()
    }
}
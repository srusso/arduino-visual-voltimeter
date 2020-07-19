package net.sr89.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import java.util.Random

class StarSupplier(private val starDensity: Float) {
    fun initStars(): Array<Vector2>? {
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height
        val starCount = (width * height * starDensity).toInt()
        val stars = Array<Vector2>(false, starCount)
        val random = Random()

        for (i in 1..starCount) {
            stars.add(Vector2(random.nextFloat(), random.nextFloat()))
        }
        return stars
    }

}
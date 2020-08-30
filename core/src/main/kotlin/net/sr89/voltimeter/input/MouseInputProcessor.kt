package net.sr89.voltimeter.input

import com.badlogic.gdx.InputProcessor

class MouseInputProcessor : InputProcessor {
    private var currentMouseX = 0
    private var currentMouseY = 0

    fun currentMouseX(): Int {
        return currentMouseX
    }

    fun currentMouseY(): Int {
        return currentMouseY
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        currentMouseX = screenX
        currentMouseY = screenX
        return true
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

}
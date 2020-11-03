package net.sr89.voltimeter.collection

import com.badlogic.gdx.utils.Queue

/**
 * A FIFO data structure that keeps at most [maxSize] elements,
 * pruning from the head (oldest).
 */
class CircularQueue<T>(private val maxSize: Int) {
    private val values: Queue<T> = Queue(maxSize)

    fun add(value: T) {
        if (values.size >= maxSize) {
            values.removeFirst()
        }

        values.addLast(value)
    }

    fun get(i: Int): T {
        return values.get(i)
    }

    fun size(): Int {
        return values.size
    }
}
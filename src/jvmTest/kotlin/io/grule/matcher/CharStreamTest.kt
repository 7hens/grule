package io.grule.matcher

import io.grule.token.CharReader
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNull

@Suppress("SpellCheckingInspection")
internal class CharStreamTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun peek() {
        val charStream = CharReader.fromString(text).toStream()
        assertEquals('0', charStream.peek(0))
        assertEquals('8', charStream.peek(8))
        assertEquals('F', charStream.peek(15))
        assertNull(charStream.peek(100))
        assertNull(charStream.peek(10000))
    }

    @Test
    fun getText() {
        val charStream = CharReader.fromString(text).toStream(2)
        charStream.peek(14)
        assertEquals("ABCDE", charStream.getText(10, 15))
    }

    @Test
    fun seekTo() {
        val charStream = CharReader.fromString(text).toStream(1)
        charStream.peek(14)
        assertEquals('8', charStream.peek(8))
        charStream.moveNext(14)
        assertEquals(14, charStream.position.index)
    }
}

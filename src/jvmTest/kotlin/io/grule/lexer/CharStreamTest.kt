package io.grule.lexer

import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("SpellCheckingInspection")
internal class CharStreamTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun peek() {
        val charStream = CharReader.fromString(text).toStream()
        assertEquals('0', charStream.peek(0).toChar())
        assertEquals('8', charStream.peek(8).toChar())
        assertEquals('F', charStream.peek(15).toChar())
        assertEquals(CharStream.EOF, charStream.peek(100))
        assertEquals(CharStream.EOF, charStream.peek(10000))
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
        assertEquals('8', charStream.peek(8).toChar())
        charStream.moveNext(14)
        assertEquals(14, charStream.charIndex)
    }
}

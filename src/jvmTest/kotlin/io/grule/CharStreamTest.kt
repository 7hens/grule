package io.grule

import io.grule.lexer.CharReader
import io.grule.lexer.CharStream
import org.junit.Test

import org.junit.Assert.*

@Suppress("SpellCheckingInspection")
internal class CharStreamTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun peek() {
        val charReader = CharReader.fromString(text)
        val charStream = CharStream(charReader)
        assertEquals('0', charStream.peek(0).toChar())
        assertEquals('8', charStream.peek(8).toChar())
        assertEquals('F', charStream.peek(15).toChar())
        assertEquals(CharStream.EOF, charStream.peek(100))
        assertEquals(CharStream.EOF, charStream.peek(10000))
    }

    @Test
    fun getText() {
        val charReader = CharReader.fromString(text)
        val charStream = CharStream(charReader, 2)
        charStream.peek(14)
        assertEquals("ABCDE", charStream.getText(10, 15))
    }

    @Test
    fun seekTo() {
        val charReader = CharReader.fromString(text)
        val charStream = CharStream(charReader, 1)
        charStream.peek(14)
        assertEquals('8', charStream.peek(8).toChar())
        charStream.moveNext(14)
        assertEquals(14, charStream.index())
    }
}
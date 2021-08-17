package io.grule

import io.grule.lexer.CharReader
import io.grule.lexer.CharStream
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("LocalVariableName")
class LexerMatcherTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun match() {
        val charReader = CharReader.fromString(text)
        val charStream = CharStream(charReader, 2)
        Grule {
            assertEquals(4, (TokenL + "0123").match(charStream, 0))
            assertEquals(10, (TokenL + DIGIT).repeat().match(charStream, 0))
            assertEquals(1, (TokenL + WORD).match(charStream, 0))
            assertEquals(10, (TokenL + "01" + ANY.until(L + "89")).match(charStream, 0))
        }
    }
}
package io.grule

import io.grule.lexer.CharReader
import io.grule.lexer.CharStream
import io.grule.lexer.Scanner.Companion.token
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("LocalVariableName")
class LexerMatcherTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun match() {
        val charStream = CharReader.fromString(text).toStream(2)
        Grule {
            assertEquals(4, (L + "0123").match(charStream, 0))
            assertEquals(10, (L + DIGIT).repeat().match(charStream, 0))
            assertEquals(1, (L + WORD).match(charStream, 0))
            assertEquals(10, (L + "01" + ANY.until(L + "89")).match(charStream, 0))
        }
    }
}

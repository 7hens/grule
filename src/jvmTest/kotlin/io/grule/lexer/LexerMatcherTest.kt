package io.grule.lexer

import io.grule.Grule
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
            assertEquals(10, (L + L_digit).repeat().match(charStream, 0))
            assertEquals(1, (L + L_word).match(charStream, 0))
            assertEquals(10, (L + "01" + L_any.until(L + "89")).match(charStream, 0))
        }
    }
}

package io.grule.lexer

import org.junit.Test
import kotlin.test.assertEquals

internal class LexerRegexTest {
    private val text = "0123456789ABCDEF你好\uD83D\uDE04\u0000\n\t "

    private fun match(pattern: String): Int {
        val charStream = CharStream.fromString(text)
        val lexerRegex = LexerRegex(pattern)
        val offset = lexerRegex.not().repeat().match(charStream, 0)
        return lexerRegex.match(charStream, offset)
    }
    
    @Test
    fun plainText() {
        assertEquals(1, match("0"))
        assertEquals(3, match("012"))
        assertEquals(2, match("你好"))
        assertEquals(1, match("\\0"))
        assertEquals(4, match("\\u4f60\\u597d\\uD83D\\uDE04"))
        assertEquals(2, match("\\u0030\\u0031"))
        assertEquals(2, match("\\x30\\x31"))
        assertEquals(2, match("\\060\\061"))
        assertEquals(2, match("\\n\\t"))
    }

    @Test
    fun characterClass() {
        assertEquals(4, match("[0123]*"))
        assertEquals(10, match("[0-9]*"))
        assertEquals(16, match("[0-9A-F]*"))
        assertEquals(10, match("\\d*"))
        assertEquals(16, match("\\w*"))
//        assertEquals(2, match("\\s*"))
//        assertEquals(16, match("[\\d\\w]*"))
    }

    @Test
    fun quantifier() {
        assertEquals(10, match("\\d*"))
        assertEquals(11, match("\\d*A"))
        assertEquals(11, match("\\d*?A"))
    }
}
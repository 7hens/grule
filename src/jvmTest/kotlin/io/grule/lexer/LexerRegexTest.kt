package io.grule.lexer

import org.junit.Test
import kotlin.test.assertEquals

internal class LexerRegexTest {
    @Test
    fun plainText() {
        assertEquals(3, match("abc", "abc"))
    }

    @Test
    fun group() {
        assertEquals(1, match("[abc]", "a"))
    }

    @Test
    fun repeat() {
        assertEquals(3, match("a*", "aaa"))
    }

    @Test
    fun range() {
        assertEquals(3, match("[a-z]*", "abc"))
    }

    private fun match(pattern: String, text: String): Int {
        return LexerRegex(pattern).match(CharStream.fromString(text))
    }
}
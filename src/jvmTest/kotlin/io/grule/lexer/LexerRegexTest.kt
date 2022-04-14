package io.grule.lexer

import org.junit.Test
import kotlin.test.assertEquals

internal class LexerRegexTest {
    private val text = "0123456789ABCDEF你好"
    
    @Test
    fun plainText() {
        assertEquals(1, match("0", text))
        assertEquals(3, match("012", text))
//        assertEquals(2, match("你好", text, 16))
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
    
    @Test
    fun escape() {
        assertEquals(2, match("\\t\\n", "\t\n"))
    }

    private fun match(pattern: String, text: String, offset: Int = 0): Int {
        return LexerRegex(pattern).match(CharStream.fromString(text), offset)
    }
}
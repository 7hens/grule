package io.grule.matcher

import io.grule.lexer.LexerMatcherRegex
import io.grule.lexer.LexerStatus
import io.grule.token.CharStream
import org.junit.Test
import kotlin.test.assertEquals

internal class LexerMatcherRegexTest {
    private val text = "0123456789ABCDEF{*你好\uD83D\uDE04\u0000\r\n\t "

    private fun match(pattern: String): Int {
        println("--------------------------- ${text.length}")
        println(text)
        println(pattern)
        val charStream = CharStream.fromString(text)
        val lexerRegex = LexerMatcherRegex(pattern)
        val status = LexerStatus(charStream)
        val result = lexerRegex.not().until(lexerRegex.test()).match(status)
        return lexerRegex.match(result).position - result.position
    }

    @Test
    fun plainText() {
        assertEquals(1, match("0"))
        assertEquals(3, match("012"))
        assertEquals(2, match("你好"))
        assertEquals(1, match("\\0"))
        assertEquals(1, match("\\{"))
        assertEquals(1, match("\\*"))
        assertEquals(4, match("\\u4f60\\u597d\\uD83D\\uDE04")) // 你好😄
        assertEquals(2, match("\\u0030\\u0031"))
        assertEquals(2, match("\\x30\\x31"))
        assertEquals(2, match("\\060\\061"))
        assertEquals(1, match("\\n"))
        assertEquals(2, match("\\n\\t"))
    }

    @Test
    fun characterClass() {
        assertEquals(4, match("[0123]+"))
        assertEquals(10, match("[0-9]+"))
        assertEquals(16, match("[0-9A-F]+"))
        assertEquals(11, match("[^0-9A-F]+"))
        assertEquals(10, match("\\d+"))
        assertEquals(16, match("\\w+"))
        assertEquals(17, match("\\d+\\w+?\\W"))
        assertEquals(4, match("\\s+"))
        assertEquals(16, match("[\\d\\w]+"))
        assertEquals(text.length, match(".+"))
    }

    @Test
    fun quantifier() {
        assertEquals(10, match("\\d+"))
        assertEquals(11, match("\\d+A"))
        assertEquals(11, match("\\d+?A"))
        assertEquals(10, match("\\d+?"))
        assertEquals(1, match("\\d?"))
        assertEquals(3, match("(012)?"))
        assertEquals(11, match("0.*?A"))
        assertEquals(11, match("0.+?A"))
        assertEquals(6, match("(789|ABC)+"))
    }

    @Test
    fun lookaround() {
        assertEquals(3, match("789(?=\\w)"))
        assertEquals(3, match("789(?!\\d)"))
    }

    @Test
    fun boundary() {
        assertEquals(16, match("\\b\\w+"))
        assertEquals(16, match("\\w+\\b"))
    }

    @Test
    fun line() {
        assertEquals(16, match("^\\w+"))
        assertEquals(2, match("^\\t "))
        assertEquals(5, match("\\uD83D\\uDE04\\0\\r\\n^"))
        assertEquals(5, match("\\uD83D\\uDE04\\0\\r\$\\n"))
        assertEquals(2, match("\\t \$"))
    }
}
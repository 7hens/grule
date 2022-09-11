package io.grule.lexer

import io.grule.token.CharStream
import org.junit.Test
import kotlin.test.assertEquals

/**
 * LexerMatcherKtTest.
 */
internal class LexerMatcherDslTest {

    private fun LexerMatcher.match(text: String): LexerMatcherStatus {
        val charStream = CharStream.fromString(text)
        val status = io.grule.lexer.LexerMatcherStatus.from(charStream)
        return status.apply(this)
    }

    @Test
    fun tokenStream() {
        val lexer = LexerFactory2()
        val abc by lexer { X + "A" or X + "BC" }
        val tokenStream = lexer.tokenStream("ABCABCABC")

        println(abc)
        println(tokenStream.all().joinToString("\n"))

        assertEquals(abc, tokenStream.peek(5).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(6).lexer)
    }

    @Test
    fun selfRight() {
        val lexer = LexerFactory2()
        val abc by lexer { X + "A" or X + "BC" self { it + "-" + me } }
        val tokenStream = lexer.tokenStream("A-BC-A-BC-A-BC")

        println(abc)
        println(tokenStream.all().joinToString("\n"))
        assertEquals(abc, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(1).lexer)
    }

    @Test
    fun selfLeft() {
        val lexer = LexerFactory2()
        val abc by lexer { X + "A" or X + "BC" self { me + "-" + it } }
        val tokenStream = lexer.tokenStream("A-BC-A-BC-A-BC")

        println(abc)
        println(tokenStream.all().joinToString("\n"))
        assertEquals(abc, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(1).lexer)
    }
}
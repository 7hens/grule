package io.grule.matcher2.lexer

import io.grule.lexer.Lexer
import io.grule.lexer.LexerFactory2
import io.grule.matcher.CharStream
import org.junit.Test
import kotlin.test.assertEquals

/**
 * LexerMatcherKtTest.
 * @author huangzhenzi
 */
internal class LexerMatcherDslTest {

    private fun LexerMatcher.match(text: String): LexerMatcherStatus {
        val charStream = CharStream.fromString(text)
        val status = LexerMatcherStatus(charStream)
        return status.apply(this)
    }

    @Test
    fun tokenStream() {
        val lexer = LexerFactory2()
        val abc by lexer.m2 { X + "A" or X + "BC" }
        val tokenStream = lexer.tokenStream("ABCABCABC")

        println(abc)
        println(tokenStream.all().joinToString("\n"))

        assertEquals(abc, tokenStream.peek(5).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(6).lexer)
    }

    @Test
    fun self() {
        val lexer = LexerFactory2()
        val abc by lexer.m2 { X + "A" or X + "BC" self { me + it } }
        val tokenStream = lexer.tokenStream("ABCABCABC")

        println(abc)
        println(tokenStream.all().joinToString("\n"))
        assertEquals(abc, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(1).lexer)
    }
}
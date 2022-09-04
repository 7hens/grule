package io.grule.matcher2.parser

import io.grule.lexer.LexerFactory2
import io.grule.matcher.CharStream
import org.junit.Test

/**
 * ParserDslTest.
 * @author huangzhenzi
 */
class ParserDslTest {
    @Test
    fun test() {
        val lexer = LexerFactory2()
        val num by lexer.m2 { digit }
        val op by lexer.m2 { X - "+-*/" }
        lexer.skip { wrap or space }

        val charStream = CharStream.fromString("1 2")
        val tokenStream = lexer.tokenStream(charStream)

        val parser = ParserFactory2()
        val exp by parser { X + num or X + op }
        val astNode = exp.parse(tokenStream)

        println(astNode.toStringTree())
    }
}
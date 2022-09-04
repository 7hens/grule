package io.grule.matcher2.parser

import io.grule.lexer.LexerFactory
import io.grule.matcher.CharStream
import org.junit.Test

/**
 * ParserDslTest.
 * @author huangzhenzi
 */
class ParserDslTest {
    @Test
    fun test() {
        val lexer = LexerFactory()
        val num by lexer.m2 { digit }
        val op by lexer.m2 { X - "+-*/" }
        lexer.skip { WRAP or SPACE }

        val charStream = CharStream.fromString("1 + 2")
        val tokenStream = lexer.tokenStream(charStream)

        val parser = ParserFactory()
        val exp by parser { X + num or X + op self { it + me } }
        val astNode = exp.parse(tokenStream)

        println(astNode.toStringTree())
    }
}
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
    fun selfLeft() {
        val lexer = LexerFactory2()
        val num by lexer.m2 { digit }
        val op by lexer.m2 { X - "+-*/" }
        lexer.skip { wrap or space }

        val charStream = CharStream.fromString("1 + 2 - 3 * 4")
        val tokenStream = lexer.tokenStream(charStream)

        val parser = ParserFactory2()
        val exp by parser { X + num self { me + op + it } }
        val astNode = exp.parse(tokenStream)

        println(astNode.toStringTree())
    }

    @Test
    fun selfRight() {
        val lexer = LexerFactory2()
        val num by lexer.m2 { digit }
        val op by lexer.m2 { X - "+-*/" }
        lexer.skip { wrap or space }

        val charStream = CharStream.fromString("1 + 2 - 3 * 4")
        val tokenStream = lexer.tokenStream(charStream)

        val parser = ParserFactory2()
        val exp by parser { X + num self { it + op + me } }
        val astNode = exp.parse(tokenStream)

        println(astNode.toStringTree())
    }
}
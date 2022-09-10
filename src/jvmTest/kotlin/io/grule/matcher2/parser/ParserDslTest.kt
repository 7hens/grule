package io.grule.matcher2.parser

import io.grule.lexer.LexerFactory2
import org.junit.Test

/**
 * ParserDslTest.
 * @author huangzhenzi
 */
class ParserDslTest {
    @Test
    fun jso() {
    }

    @Test
    fun selfLeft() {
        val source = "1 + 2 - 3 * 4"
        println(source)
        println("X + num self { me + op + it }")

        val lexer = LexerFactory2()
        val num by lexer { digit }
        val op by lexer { X - "+-*/" }
        lexer.skip { wrap or space }

        val parser = ParserFactory2()
        val exp by parser { X + num self { me + op + it } }
        val astNode = exp.parse(lexer.tokenStream(source))

        println(astNode.toStringTree())
    }

    @Test
    fun selfRight() {
        val source = "1 + 2 - 3 * 4"
        println(source)
        println("X + num self { it + op + me }")

        val lexer = LexerFactory2()
        val num by lexer { digit }
        val op by lexer { X - "+-*/" }
        lexer.skip { wrap or space }

        val parser = ParserFactory2()
        val exp by parser { X + num self { it + op + me } }
        val astNode = exp.parse(lexer.tokenStream(source))

        println(astNode.toStringTree())
    }
}
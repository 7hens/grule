package io.grule.parser

import io.grule.lexer.LexerFactory
import org.junit.Test

/**
 * ParserDslTest.
 */
class ParserDslTest {
    @Test
    fun selfLeft() {
        val source = "1 + 2 - 3 * 4"
        println(source)
        println("X + num self { me + op + it }")

        val lexer = LexerFactory()
        val num by lexer { DIGIT }
        val op by lexer { X - "+-*/" }
        lexer.skip { WRAP or SPACE }

        val parser = ParserFactory()
        val exp by parser { X + num self { me + op + it } }
        val astNode = exp.parse(lexer.tokenStream(source))

        println(astNode.toStringTree())
    }

    @Test
    fun selfRight() {
        val source = "1 + 2 - 3 * 4"
        println(source)
        println("X + num self { it + op + me }")

        val lexer = LexerFactory()
        val num by lexer { DIGIT }
        val op by lexer { X - "+-*/" }
        lexer.skip { WRAP or SPACE }

        val parser = ParserFactory()
        val exp by parser { X + num self { it + op + me } }
        val astNode = exp.parse(lexer.tokenStream(source))

        println(astNode.toStringTree())
    }
}
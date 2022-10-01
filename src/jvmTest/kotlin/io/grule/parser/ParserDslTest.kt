package io.grule.parser

import io.grule.lexer.Lexer
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

        val lexer = Lexer.factory()
        val num by lexer { DIGIT }
        val op by lexer { X["+-*/"] }
        lexer.skip { WRAP or SPACE }

        val parser = Parser.factory(lexer)
        val exp by parser { X + num self { me + op + it } }
        val astNode = exp.parse(source)

        println(astNode.toStringTree())
    }

    @Test
    fun selfRight() {
        val source = "1 + 2 - 3 * 4"
        println(source)
        println("X + num self { it + op + me }")

        val lexer = Lexer.factory()
        val num by lexer { DIGIT }
        val op by lexer { X["+-*/"] }
        lexer.skip { WRAP or SPACE }

        val parser = Parser.factory(lexer)
        val exp by parser { X + num self { it + op + me } }
        val astNode = exp.parse(source)

        println(astNode.toStringTree())
    }
}
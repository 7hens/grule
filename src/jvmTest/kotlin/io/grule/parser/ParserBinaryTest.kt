package io.grule.parser

import org.junit.Test
import kotlin.test.assertEquals

class ParserBinaryTest {
    @Test
    fun simple() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { (X + N + O).until(X + "x") }
            val main by parser { X + exp.binary(O) }

            val astNode = main.parse(tokenStream(source))
            println("--------------------------------------")
            println(source)
            println(astNode.toStringExpr())
            println(astNode.toStringTree())
            assertEquals("(((0 * 1) + (2 * 3)) - (4 / x))", astNode.toStringExpr())
        }
    }

    @Test
    fun missingElements() {
        RepeatGrammar().apply {
            val source = "* 1 + 2 * 3 * - 4 /"
            val exp by parser { (X + O).more().repeat().join(X + N) }
            val main by parser { X + exp.binary(O) }

            val astNode = main.parse(tokenStream(source))
            println("--------------------------------------")
            println(source)
            println(astNode.toStringExpr())
            println(astNode.toStringTree())
            assertEquals("(((() * 1) + ((2 * 3) * ())) - (4 / ()))", astNode.toStringExpr())
        }
    }

    @Test
    fun missingOperators() {
        RepeatGrammar().apply {
            val source = "1 2 3 + 2 * 3 - 4 / x"
            val exp by parser { (X + (X + N).more() + O).until(X + "x") }
            val main by parser { X + exp.binary(O) }

            val astNode = main.parse(tokenStream(source))
            println("--------------------------------------")
            println(source)
            println(astNode.toStringExpr())
            println(astNode.toStringTree())
            assertEquals("(((1 2 3) + (2 * 3)) - (4 / x))", astNode.toStringExpr())
        }
    }
}
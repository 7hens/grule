package io.grule.parser

import org.junit.Test
import kotlin.test.assertEquals

class ParserBinaryTest {
    @Test
    fun simple() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { (X + Num + Op).untilNonGreedy(X + "x") }
            val main by parser { X + exp.binary(Op) }

            val astNode = main.parse(this, source)
            println("--------------------------------------")
            println(source)
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((0 * 1) + (2 * 3)) - (4 / x))", astNode.toStringLine())
        }
    }

    @Test
    fun missingElements() {
        RepeatGrammar().apply {
            val source = "* 1 + 2 * 3 * - 4 /"
            val exp by parser { (X + Op).repeat(1).join(X + Num) }
            val main by parser { X + exp.binary(Op) }

            val astNode = main.parse(this, source)
            println("--------------------------------------")
            println(source)
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((() * 1) + ((2 * 3) * ())) - (4 / ()))", astNode.toStringLine())
        }
    }

    @Test
    fun missingOperators() {
        RepeatGrammar().apply {
            val source = "1 2 3 + 2 * 3 - 4 / x"
            val exp by parser { (X + (X + Num).repeat(1) + Op).untilNonGreedy(X + "x") }
            val main by parser { X + exp.binary(Op) }

            val astNode = main.parse(this, source)
            println("--------------------------------------")
            println(source)
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((1 2 3) + (2 * 3)) - (4 / x))", astNode.toStringLine())
        }
    }

    @Test
    fun tree() {
        RepeatGrammar().apply {
            val source = "1 + 2 * 3 + 4"
            val exp by parser { X + Num or it + "+" + it or it + "*" + it }
            val main by parser { X + exp.binary { it.firstToken!!.text in listOf("*", "+") } }

            val astNode = main.parse(this, source)
            println("--------------------------------------")
            println(source)
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("((1 + (2 * 3)) + 4)", astNode.toStringLine())
        }
    }
}
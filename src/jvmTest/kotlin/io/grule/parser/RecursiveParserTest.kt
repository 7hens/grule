package io.grule.parser

import org.junit.Test
import kotlin.test.assertEquals

class RecursiveParserTest {

    @Test
    fun left() {
        RepeatGrammar().apply {
            val source = "0 1 2 3"
            println("================")
            println(source)
            println("X + Num self { me + it }")

            val exp by parser { X + Num self { me + it } }

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((0 1) 2) 3)", astNode.toStringLine())
        }
    }

    @Test
    fun leftBinary() {
        RepeatGrammar().apply {
            val source = "0 * 1 / x"
            println("================")
            println(source)
            println("X + \"x\" or X + Num self { me + Op + it }")

            val exp by parser { X + "x" or X + Num self { me + Op + it } }

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("((0 * 1) / x)", astNode.toStringLine())
        }
    }

    @Test
    fun right() {
        RepeatGrammar().apply {
            val source = "0 1 2 3"
            println("================")
            println(source)
            println("X + Num self { it + me }")

            val exp by parser { X + Num self { it + me } }

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(0 (1 (2 3)))", astNode.toStringLine())
        }
    }

    @Test
    fun rightBinary() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            println("================")
            println(source)
            println("{ X + \"x\" or X + Num self { X + Num + Op + me } }")

            val exp by parser { X + "x" or X + Num self { X + Num + Op + me } }

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(0 * (1 + (2 * (3 - (4 / x)))))", astNode.toStringLine())
        }
    }

    @Test
    fun cascadingLeft() {
        RepeatGrammar().apply {
            val source = "0 + 1 2 3"
            println("================")
            println(source)
            println("X + Num self { me + Op } self { me + Num }")

            val exp by parser { X + Num self { me + Op } self { me + Num } }

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("((((0 +) 1) 2) 3)", astNode.toStringLine())
        }
    }

    @Test
    fun or() {
        RepeatGrammar().apply {
            val source = "0 + 1 2 - 3"
            println("================")
            println(source)
            println("X + Num self { me + it or me + Op }")

            val exp by parser { X + Num self { me + it or me + Op } }

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((((0 +) 1) 2) -) 3)", astNode.toStringLine())
        }
    }

    @Test
    fun thenBinaryOp() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            println("================")
            println(source)
            println("X + Num or X + \"x\" self { me + Op + it }")

            val exp by parser { X + Num or X + "x" self { me + Op + it } }
            val binary by parser { exp.flat().binary(Op) }

            val astNode = binary.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((0 * 1) + (2 * 3)) - (4 / x))", astNode.toStringLine())
        }
    }

    @Test
    fun cascadingBinary() {
        RepeatGrammar().apply {
            val source = "1 + 2 * 3 * 4 + 5"
            println("--------------------------------------")
            println(source)
            println("X + Num self { me + \"*\" + it } self { me + \"+\" + it }")

            val exp by parser { X + Num self { me + "*" + it } self { me + "+" + it } }
            val main by parser { X + exp }
            val astNode = main.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("((1 + ((2 * 3) * 4)) + 5)", astNode.toStringLine())
        }
    }
}
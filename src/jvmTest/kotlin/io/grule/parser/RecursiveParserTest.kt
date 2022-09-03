package io.grule.parser

import org.junit.Test
import kotlin.test.assertEquals

class RecursiveParserTest {

    @Test
    fun recursiveError() {

    }

    @Test(expected = ParserException::class)
    fun recursiveLeft() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { X + it + "x" or it + Num or it + Op }

            val astNode = exp.parse(this, source)
            println("================")
            println(astNode.toStringTree())
        }
    }

    @Test
    fun recursiveRight() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { X + Num + Op + it or X + "x" or X + Num }

            val astNode = exp.parse(this, source)
            println("================")
            println(astNode.toStringTree())
        }
    }

    @Test
    fun recursiveBinary() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { X + Num or X + "x" or it + Op + it }
            val binary by parser { exp.flat().binary(Op) }

            val astNode = binary.parse(this, source)
            println("================")
            println(source)
            println(astNode.toStringTree())
        }
    }

    @Test
    fun recursiveSelf() {
        RepeatGrammar().apply {
            val source = "0 1 2 3"
            val exp by parser { X + Num or it + it }

            val astNode = exp.parse(this, source)
            println("================")
            println(source)
            println("================")
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(0 (1 (2 3)))", astNode.toStringLine())
        }

        RepeatGrammar().apply {
            val source = "0 1 2 3"
            val exp by parser { X + Num or it + X + it }

            val astNode = exp.parse(this, source)
            println("================")
            println(source)
            println("================")
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((0 1) 2) 3)", astNode.toStringLine())
        }
    }
}
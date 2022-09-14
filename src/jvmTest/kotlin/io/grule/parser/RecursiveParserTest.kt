package io.grule.parser

import org.junit.Test
import kotlin.test.assertEquals

class RecursiveParserTest {

    @Test
    fun recursiveRight() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { X + "x" or X + Num self { X + Num + Op + me } }

            val astNode = exp.parse(tokenStream(source))
            println("================")
            println(astNode.toStringTree())
        }
    }

    @Test
    fun recursiveBinary() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { X + Num or X + "x" self { me + Op + it } }
            val binary by parser { exp.flat().binary(Op) }

            val astNode = binary.parse(tokenStream(source))
            println("================")
            println(source)
            println(astNode.toStringTree())
        }
    }

    @Test
    fun recursiveSelf() {
        RepeatGrammar().apply {
            val source = "0 1 2 3"
            val exp by parser { X + Num self { it + me } }

            println("================")
            println(source)
            println("================")

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(0 (1 (2 3)))", astNode.toStringLine())
        }

        RepeatGrammar().apply {
            val source = "0 1 2 3"
            val exp by parser { X + Num self { me + it } }

            println("================")
            println(source)
            println("================")

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((0 1) 2) 3)", astNode.toStringLine())
        }
    }

    @Test
    fun recursiveSelfSelf() {
        RepeatGrammar().apply {
            val source = "0 + 1 2 3"
            val exp by parser { X + Num self { me + Op } self { me + Num } }

            println("================")
            println(source)
            println("X + Num self { me + Op } self { me + Num }")
            println("================")

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("((((0 +) 1) 2) 3)", astNode.toStringLine())
        }
    }

    @Test
    fun recursiveSelfOr() {
        RepeatGrammar().apply {
            val source = "0 + 1 2 - 3"
            val exp by parser { X + Num self { me + it or me + Op } }

            println("================")
            println(source)
            println("================")

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringLine())
            println(astNode.toStringTree())
            assertEquals("(((((0 +) 1) 2) -) 3)", astNode.toStringLine())
        }
    }
}
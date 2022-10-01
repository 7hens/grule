package io.grule.parser

import io.grule.util.log.Logger
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class RecursiveParserTest {
    @Before
    fun before() {
        Logger.setDefault(Logger.builder().verbose)
    }

    @Test
    fun itMe() {
        RepeatGrammar().apply {
            val source = "0 1 2 3 4 5 6"
            println("================")
            println(source)
            println("X + Num self { it + me }")

            val e by parser { X + N self { it + me } }

            val astNode = e.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("(0 (1 (2 (3 (4 (5 6))))))", astNode.toStringExp())
            assertEquals(
                "e(e(N(0)) e(e(N(1)) e(e(N(2)) e(e(N(3)) e(e(N(4)) e(e(N(5)) e(N(6))))))))",
                astNode.toString()
            )
        }
    }

    @Test
    fun itOpMe() {
        RepeatGrammar().apply {
            val source = "x * 1 / 2 - 3 + 4 / 5"
            println("================")
            println(source)
            println("X + \"x\" or X + Num self { it + Op + me }")

            val e by parser { X + "x" or X + N self { it + O + me } }

            val astNode = e.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("(x * (1 / (2 - (3 + (4 / 5)))))", astNode.toStringExp())
            assertEquals(
                "e(e(x) O(*) e(e(N(1)) O(/) e(e(N(2)) O(-) e(e(N(3)) O(+) e(e(N(4)) O(/) e(N(5)))))))",
                astNode.toString()
            )
        }
    }

    @Test
    fun meIt() {
        RepeatGrammar().apply {
            val source = "0 1 2 3"
            println("================")
            println(source)
            println("X + Num self { me + it }")

            val e by parser { X + N self { me + it } }

            val astNode = e.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("(((0 1) 2) 3)", astNode.toStringExp())
            assertEquals("e(e(e(e(N(0)) e(N(1))) e(N(2))) e(N(3)))", astNode.toString())
        }
    }

    @Test
    fun meOpIt() {
        RepeatGrammar().apply {
            val source = "0 * 1 / x"
            println("================")
            println(source)
            println("X + \"x\" or X + Num self { me + Op + it }")

            val e by parser { X + "x" or X + N self { me + O + it } }

            val astNode = e.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("((0 * 1) / x)", astNode.toStringExp())
            assertEquals("e(e(e(N(0)) O(*) e(N(1))) O(/) e(x))", astNode.toString())
        }
    }

    @Test
    fun meOp_meNum() {
        RepeatGrammar().apply {
            val source = "0 + 1 2 3"
            println("================")
            println(source)
            println("X + Num self { me + Op } self { me + Num }")

            val e by parser { X + N self { me + O } self { me + N } }

            val astNode = e.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("((((0 +) 1) 2) 3)", astNode.toStringExp())
            assertEquals("e(e(e(e(e(N(0)) O(+)) N(1)) N(2)) N(3))", astNode.toString())
        }
    }

    @Test
    fun meItOrMeOp() {
        RepeatGrammar().apply {
            val source = "0 + 1 2 - 3"
            println("================")
            println(source)
            println("X + Num self { me + it or me + Op }")

            val e by parser { X + N self { me + it or me + O } }

            val astNode = e.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("(((((0 +) 1) 2) -) 3)", astNode.toStringExp())
            assertEquals("e(e(e(e(e(e(N(0)) O(+)) e(N(1))) e(N(2))) O(-)) e(N(3)))", astNode.toString())
        }
    }

    @Test
    fun meOp_meIt() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4"
            println("================")
            println(source)
            println(" X + N self { me + O } self { me + it }")

            val e by parser { X + N self { me + O } self { me + it } }

            val astNode = e.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("(((((0 *) (1 +)) (2 *)) (3 -)) 4)", astNode.toStringExp())
            assertEquals(
                "e(e(e(e(e(e(N(0)) O(*)) e(e(N(1)) O(+))) e(e(N(2)) O(*))) e(e(N(3)) O(-))) e(N(4)))",
                astNode.toString()
            )
        }
    }

    @Test
    fun meOpIt_binary() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            println("================")
            println(source)
            println("X + Num or X + \"x\" self { me + Op + it }")

            val e by parser { X + N or X + "x" self { me + O + it } }
            val b by parser { e.flat().binary(O) }

            val astNode = b.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("(((0 * 1) + (2 * 3)) - (4 / x))", astNode.toStringExp())
            assertEquals(
                "b(e(e(e(e(N(0)) O(*) e(N(1))) O(+) e(e(N(2)) O(*) e(N(3)))) O(-) e(e(N(4)) O(/) e(x))))",
                astNode.toString()
            )
        }
    }

    @Test
    fun meXIt_meXIt() {
        RepeatGrammar().apply {
            val source = "1 + 2 * 3 * 4 + 5"
            println("--------------------------------------")
            println(source)
            println("X + Num self { me + \"*\" + it } self { me + \"+\" + it }")

            val e by parser { X + N self { me + "*" + it } self { me + "+" + it } }
            val m by parser { X + e }
            val astNode = m.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("((1 + ((2 * 3) * 4)) + 5)", astNode.toStringExp())
            assertEquals(
                "m(e(e(e(N(1)) + e(e(e(N(2)) * e(N(3))) * e(N(4)))) + e(N(5))))",
                astNode.toString()
            )
        }
    }

    @Test
    fun numOpMe() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            println("================")
            println(source)
            println("{ X + \"x\" or X + Num self { X + Num + Op + me } }")

            val e by parser { X + "x" or X + N self { X + N + O + me } }

            val astNode = e.parse(source)
            println(astNode.toStringExp())
            println(astNode.toString())
            println(astNode.toStringTree())
            assertEquals("(0 * (1 + (2 * (3 - (4 / x)))))", astNode.toStringExp())
            assertEquals(
                "e(N(0) O(*) e(N(1) O(+) e(N(2) O(*) e(N(3) O(-) e(N(4) O(/) e(x))))))",
                astNode.toString()
            )
        }
    }

}
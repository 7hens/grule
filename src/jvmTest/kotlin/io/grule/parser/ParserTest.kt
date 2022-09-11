package io.grule.parser

import io.grule.Grammar
import org.junit.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun plus() {
        val source = "abc"
        Grammar {
            val A by lexer { X + "a" }
            lexer.token { WORD }

            val a by parser { X + A }
            val b by parser { X + "b" }
            val b2 by parser { X + b }
            val abc by parser { X + a + b2 + "c" }

            val node = abc.parse(tokenStream(source))
            println(node.toStringTree())
            assertEquals(1, node.all(a).size)
            assertEquals(1, node.all(b2).size)
            assertEquals(1, node.all("c").size)
        }
    }

    @Test
    fun or() {
        val source = "abcd"
        Grammar {
            val A by lexer { X + "a" }
            val Bc by lexer { X + "bc" }
            val D by lexer { X + "d" }

            val e by parser { X + "e" }
            val bc by parser { X + Bc }
            val eOrBc by parser { X + e or bc }
            val d by parser { X + D }
            val abcd by parser { X + A + eOrBc + d }

            val node = abcd.parse(tokenStream(source))
            println(node.toStringTree())
            assertEquals(1, node.all(A).size)
            assertEquals(1, node.all(eOrBc).size)
            assertEquals(1, node.all(d).size)
        }
    }

    @Test
    fun repeat() {
        val source = "0123456789"
        Grammar {
            val t1 by lexer { X + "01" }
            val t2 by lexer { DIGIT }

            val digit by parser { X + t2 }
            val parser by parser { X + t1 + digit.repeat() }

            val node = parser.parse(tokenStream(source))
            println(node.toStringTree())
            assertEquals(1, node.all(t1).size)
            assertEquals(8, node.all(digit).size)
        }
    }

    @Test
    fun repeatWith() {
        val source = "017,8,9"
        Grammar {
            val t1 by lexer { X + "01" }
            val t2 by lexer { DIGIT }
            lexer.token { X + "," }

            val digit by parser { X + t2 }
            val parser by parser { X + t1 + digit.join(X + ",") }

            val node = parser.parse(tokenStream(source))
            println(node.toStringTree())
            assertEquals(1, node.all(t1).size)
            assertEquals(3, node.all(digit).size)
            assertEquals(2, node.all(",").size)
        }
    }

    @Test
    fun builder() {
        val source = "012345"
        Grammar {
            lexer.token { X + "0123" }
            lexer.token { X + "45" }

            val parser by parser { X + "0123" + (X + "45") }
            val node = parser.parse(tokenStream(source))
            println(node.toStringTree())
            assertEquals(1, node.all("0123").size)
            assertEquals(1, node.all("45").size)
        }
    }

    private class RepeatGrammar : Grammar() {
        val Num by lexer { DIGIT.repeat(1) }
        val Op by lexer { X - "+-*/%><=!" }

        init {
            lexer.skip { WRAP or SPACE }
            lexer.token { X + "x" }
        }
    }

    @Test
    fun untilGreedy() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { (X + Num + Op).untilGreedy(X + "x") }

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringTree())
        }
    }

    @Test
    fun untilReluctant() {
        RepeatGrammar().apply {
            val source = "0 * 1 + 2 * 3 - 4 / x"
            val exp by parser { (X + Num + Op).untilNonGreedy(X + "x") }

            val astNode = exp.parse(tokenStream(source))
            println(astNode.toStringTree())
        }
    }
}

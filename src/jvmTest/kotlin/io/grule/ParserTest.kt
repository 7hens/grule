package io.grule

import io.grule.lexer.CharReader
import io.grule.lexer.CharStream
import org.junit.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun plus() {
        val input = "abc"
        val charReader = CharReader.fromString(input)
        val charStream = CharStream(charReader, 2)

        Grule {
            val A by TokenL + "a"
            TokenL + WORD

            val a by P + A
            val b by P + "b"
            val b2 by P + b
            val abc by P + a + b2 + "c"
            val node = abc.parse(charStream)

            print(node)
            assertEquals(1, node.all(a).size)
            assertEquals(1, node.all(b2).size)
            assertEquals(1, node.all("c").size)
        }
    }

    @Test
    fun or() {
        val input = "abcd"
        val charReader = CharReader.fromString(input)
        val charStream = CharStream(charReader, 2)

        Grule {
            val A by TokenL + "a"
            val Bc by TokenL + "bc"
            val D by TokenL + "d"

            val e by P + "e"
            val bc by P + Bc
            val eOrBc by P + e or bc
            val d by P + D
            val abcd by P + A + eOrBc + d

            val node = abcd.parse(charStream)
            println(node.toStringTree())
            assertEquals(1, node.all(A).size)
            assertEquals(1, node.all(eOrBc).size)
            assertEquals(1, node.all(d).size)
        }
    }

    @Test
    fun repeat() {
        val input = "0123456789"
        val charReader = CharReader.fromString(input)
        val charStream = CharStream(charReader, 2)

        Grule {
            val t1 by TokenL + "01"
            val t2 by TokenL + DIGIT

            val digit by P + t2
            val parser by P + t1 + digit.repeat()
            val node = parser.parse(charStream)
            println(node.toStringTree())
            assertEquals(1, node.all(t1).size)
            assertEquals(8, node.all(digit).size)
        }
    }

    @Test
    fun repeatWith() {
        val input = "017,8,9"
        val charReader = CharReader.fromString(input)
        val charStream = CharStream(charReader, 2)

        Grule {
            val t1 by TokenL + "01"
            val t2 by TokenL + DIGIT
            TokenL + ","

            val digit by P + t2
            val parser by P + t1 + digit.repeatWith(P + ",")
            val node = parser.parse(charStream)
            println(node.toStringTree())
            assertEquals(1, node.all(t1).size)
            assertEquals(3, node.all(digit).size)
            assertEquals(2, node.all(",").size)
        }
    }

    @Test
    fun builder() {
        val input = "012345"
        val charReader = CharReader.fromString(input)
        val charStream = CharStream(charReader, 2)

        Grule {
            TokenL + "0123"
            TokenL + "45"

            val parser by P + "0123" + (P + "45")
            val node = parser.parse(charStream)
            println(node.toStringTree())
            assertEquals(1, node.all("0123").size)
            assertEquals(1, node.all("45").size)
        }
    }

    @Test
    fun json() {
        val input = """{ "a": [1, 2.34], "b": "hello" }"""
        val charStream = CharStream(CharReader.fromString(input))

        Grule {
            val string by TokenL + '"' + ANY.until(L + '"')
            val float by TokenL + DIGIT.repeat(1) + "." + DIGIT.repeat(1)
            val integer by TokenL + DIGIT.repeat(1)
            val bool by TokenL + "true" or L + "false"
            val nil by TokenL + "null"

            TokenL + -"{}[]:,"
            SkipL + SPACE or LINE

            val jObject by P
            val jString by P + string
            val jInteger by P + integer
            val jFloat by P + float
            val jBool by P + bool
            val jNil by P + nil
            val jArray by P + "[" + jObject.repeatWith(P + ",").optional() + "]"
            val jPair by P + jString + ":" + jObject
            val jDict by P + "{" + jPair.repeatWith(P + ",").optional() + "}"
            jObject or jString or jFloat or jInteger or jBool or jNil or jArray or jDict

            val astNode = jObject.parse(charStream)
            println(astNode.toStringTree())
        }
    }
}
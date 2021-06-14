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
            assertEquals(1, node[a].size)
            assertEquals(1, node[b2].size)
            assertEquals(1, node["c"].size)
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
            println(node)
            assertEquals(1, node[A].size)
            assertEquals(1, node[eOrBc].size)
            assertEquals(1, node[d].size)
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
            println(node)
            assertEquals(1, node[t1].size)
            assertEquals(8, node[digit].size)
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
            println(node)
            assertEquals(1, node[t1].size)
            assertEquals(3, node[digit].size)
            assertEquals(2, node[","].size)
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
            println(node)
            assertEquals(1, node["0123"].size)
            assertEquals(1, node["45"].size)
        }
    }

    @Test
    fun json() {
//        val input = "\"hello\""
//        val input = "123"
        val input = """{ "a": [1, 2, 3], "b": "hello" }"""
//        val input = """{ "b": "hello" }"""
        val charReader = CharReader.fromString(input)
        val charStream = CharStream(charReader, 2)

        Grule {
            val string by TokenL + '"' + ANY.until(L + '"')
            val integer by TokenL + DIGIT.repeat(1)
            val float by TokenL + DIGIT.repeat(1) + "." + DIGIT.repeat(1)
            val bool by TokenL + "true" or L + "false"
            val nil by TokenL + "null"

            TokenL + -"{}[]:,"
            SkipL + SPACE or LINE

            println("hello, json")

            val jObject by P
            val jString by P + string
            val jInteger by P + integer
            val jFloat by P + float
            val jBool by P + bool
            val jNil by P + nil
            val jArray by P + "[" + jObject.repeatWith(",").optional() + "]"
            val jPair by P + jString + ":" + jObject
            val jDict by P + "{" + jPair.repeatWith(",").optional() + "}"
            jObject or jString or jFloat or jInteger or jBool or jNil or jArray or jDict

            val node = jObject.parse(charStream)
            println(node)
        }
    }
}
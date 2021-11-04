package io.grule

import io.grule.lexer.CharReader
import io.grule.lexer.CharStream
import io.grule.lexer.Scanner.Companion.skip
import io.grule.lexer.Scanner.Companion.token
import org.junit.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun plus() {
        val source = "abc"
        val charStream = CharReader.fromString(source).toStream(2)

        Grule {
            val A by token(L + "a")
            token(L + WORD)

            val a by P + A
            val b by P + "b"
            val b2 by P + b
            val abc by P + a + b2 + "c"
            val node = parse(abc, charStream)

            print(node)
            assertEquals(1, node.all(a).size)
            assertEquals(1, node.all(b2).size)
            assertEquals(1, node.all("c").size)
        }
    }

    @Test
    fun or() {
        val source = "abcd"
        val charStream = CharReader.fromString(source).toStream(2)

        Grule {
            val A by token(L + "a")
            val Bc by token(L + "bc")
            val D by token(L + "d")

            val e by P + "e"
            val bc by P + Bc
            val eOrBc by P + e or bc
            val d by P + D
            val abcd by P + A + eOrBc + d

            val node = parse(abcd, charStream)
            println(node.toStringTree())
            assertEquals(1, node.all(A).size)
            assertEquals(1, node.all(eOrBc).size)
            assertEquals(1, node.all(d).size)
        }
    }

    @Test
    fun repeat() {
        val source = "0123456789"
        val charStream = CharReader.fromString(source).toStream(2)

        Grule {
            val t1 by token(L + "01")
            val t2 by token(L + DIGIT)

            val digit by P + t2
            val parser by P + t1 + digit.repeat()
            val node = parse(parser, charStream)
            println(node.toStringTree())
            assertEquals(1, node.all(t1).size)
            assertEquals(8, node.all(digit).size)
        }
    }

    @Test
    fun repeatWith() {
        val source = "017,8,9"
        val charStream = CharReader.fromString(source).toStream(2)

        Grule {
            val t1 by token(L + "01")
            val t2 by token(L + DIGIT)
            token(L + ",")

            val digit by P + t2
            val parser by P + t1 + digit.repeatWith(P + ",")
            val node = parse(parser, charStream)
            println(node.toStringTree())
            assertEquals(1, node.all(t1).size)
            assertEquals(3, node.all(digit).size)
            assertEquals(2, node.all(",").size)
        }
    }

    @Test
    fun builder() {
        val source = "012345"
        val charStream = CharReader.fromString(source).toStream(2)

        Grule {
            token(L + "0123")
            token(L + "45")

            val parser by P + "0123" + (P + "45")
            val node = parse(parser, charStream)
            println(node.toStringTree())
            assertEquals(1, node.all("0123").size)
            assertEquals(1, node.all("45").size)
        }
    }

    @Test
    fun json() {
        val source = """{ "a": [1, 2.34], "b": "hello" }"""
        println(source)
        println("-----------------")

        Grule {
            val string by token(L + '"' + ANY.until(L + '"'))
            val float by token(L + DIGIT.repeat(1) + "." + DIGIT.repeat(1))
            val integer by token(L + DIGIT.repeat(1))
            val bool by token(L + "true" or L + "false")
            val nil by token(L + "null")

            token(L + -"{}[]:,")
            skip(L + SPACE or LINE)

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

            val charStream = CharReader.fromString(source).toStream()
            val astNode = parse(jObject, charStream)
            println(astNode.toStringTree())
        }
    }
}

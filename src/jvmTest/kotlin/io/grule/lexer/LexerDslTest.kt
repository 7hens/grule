package io.grule.lexer

import org.junit.Test
import kotlin.test.assertEquals

/**
 * LexerMatcherKtTest.
 */
internal class LexerDslTest {

    @Test
    fun tokenStream() {
        val lexer = Lexer.factory()
        val abc by lexer { X + "A" or X + "BC" }
        val tokenStream = lexer.tokenStream("ABCABCABC")

        println(abc)
        println(tokenStream.all().joinToString("\n"))

        assertEquals(abc, tokenStream.peek(5).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(6).lexer)
    }

    @Test
    fun selfRight() {
        val source = "A-BC-A-BC-A-BC"
        println(source)
        println("{ X + \"A\" or X + \"BC\" self { it + \"-\" + me } }")

        val lexer = Lexer.factory()
        val abc by lexer { X + "A" or X + "BC" self { it + "-" + me } }
        val tokenStream = lexer.tokenStream(source)

        abc.toString()
        println(tokenStream.all().joinToString("\n"))
        assertEquals(abc, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(1).lexer)
    }

    @Test
    fun selfLeft() {
        val source = "A-BC-A-BC-A-BC"
        println(source)
        println("X + \"A\" or X + \"BC\" self { me + \"-\" + it }")

        val lexer = Lexer.factory()
        val abc by lexer { X + "A" or X + "BC" self { me + "-" + it } }
        val tokenStream = lexer.tokenStream(source)

        abc.toString()
        println(tokenStream.all().joinToString("\n"))
        assertEquals(abc, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(1).lexer)
    }

    @Test
    fun indent() {
        val lexer = Lexer.factory()
        val indent by lexer { X + "\\{" }
        val dedent by lexer { X + "\\}" }
        val newLine by lexer { X }
        val a by lexer { X + "A" }
        lexer.add(Lexer.indent(newLine, indent, dedent))
        val source =
            """
            |A
            |    A
            |A
            """.trimMargin()
        val tokenStream = lexer.tokenStream(source)
        println(source)
        a.toString()
        println(tokenStream.all().joinToString("\n"))
        assertEquals(a, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(6).lexer)
    }

    @Test
    fun move() {
        val source = "ABC12345"
        println(source)
        println("X + 3 + \"123\" + -1 + \"345\"")

        val lexer = Lexer.factory()
        val move by lexer { X + 3 + "123" + -1 + "345" }
        val tokenStream = lexer.tokenStream(source)

        move.toString()
        println(tokenStream.all().joinToString("\n"))
        assertEquals(move, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(1).lexer)
    }
}
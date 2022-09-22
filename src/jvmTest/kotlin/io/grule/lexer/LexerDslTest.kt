package io.grule.lexer

import org.junit.Test
import kotlin.test.assertEquals

/**
 * LexerMatcherKtTest.
 */
internal class LexerDslTest {

    @Test
    fun tokenStream() {
        val lexer = LexerFactory()
        val abc by lexer { X + "A" or X + "BC" }
        val tokenStream = lexer.tokenStream("ABCABCABC")

        println(abc)
        println(tokenStream.all().joinToString("\n"))

        assertEquals(abc, tokenStream.peek(5).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(6).lexer)
    }

    @Test
    fun selfRight() {
        val lexer = LexerFactory()
        val abc by lexer { X + "A" or X + "BC" self { it + "-" + me } }
        val tokenStream = lexer.tokenStream("A-BC-A-BC-A-BC")

        println(abc)
        println(tokenStream.all().joinToString("\n"))
        assertEquals(abc, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(1).lexer)
    }

    @Test
    fun selfLeft() {
        val source = "A-BC-A-BC-A-BC"
        val lexer = LexerFactory()
        val abc by lexer { X + "A" or X + "BC" self { me + "-" + it } }
        val tokenStream = lexer.tokenStream(source)

        abc.toString()
        println(source)
        println("X + \"A\" or X + \"BC\" self { me + \"-\" + it }")
        println(tokenStream.all().joinToString("\n"))
        assertEquals(abc, tokenStream.peek(0).lexer)
        assertEquals(Lexer.EOF, tokenStream.peek(1).lexer)
    }

    @Test
    fun indent() {
        val lexer = LexerFactory()
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
}
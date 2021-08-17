package io.grule

import io.grule.lexer.CharReader
import io.grule.lexer.CharStream
import io.grule.lexer.Lexer
import io.grule.lexer.TokenChannel
import org.junit.Assert.assertEquals
import org.junit.Test

class LexerTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun match() {
        val charReader = CharReader.fromString(text)
        val charStream = CharStream(charReader, 2)
        Grule {
            val t1 by TokenL + "01"
            val t2 by TokenL + DIGIT.repeat(1)
            val t3 by TokenL + ANY
            println(t1.name)
            println(t2.name)
            println(t3.name)

            val scanner = TokenChannel(charStream, this)
            assertEquals(t1, scanner.peek(0).lexer)
            assertEquals(t2, scanner.peek(1).lexer)
            assertEquals(t3, scanner.peek(2).lexer)
            assertEquals(Lexer.EOF, scanner.peek(15).lexer)
            println(scanner)
        }
    }
}
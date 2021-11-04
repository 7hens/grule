package io.grule

import io.grule.lexer.CharReader
import io.grule.lexer.Lexer
import org.junit.Assert.assertEquals
import org.junit.Test

class LexerTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun match() {
        val charStream = CharReader.fromString(text).toStream(2)
        Grule {
            val t1 by token(L + "01")
            val t2 by token(L + DIGIT.repeat(1))
            val t3 by token(L + ANY)
            println(t1.name)
            println(t2.name)
            println(t3.name)

            val scanner = tokenStream(charStream)
            assertEquals(t1, scanner.peek(0).scanner)
            assertEquals(t2, scanner.peek(1).scanner)
            assertEquals(t3, scanner.peek(2).scanner)
            assertEquals(Lexer.EOF, scanner.peek(15).scanner)
            println(scanner)
        }
    }
}

package io.grule.lexer

import io.grule.Grule
import org.junit.Assert.assertEquals
import org.junit.Test

class LexerSimpleTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun matchLexer() {
        val charStream = CharReader.fromString(text).toStream(2)
        Grule {
            assertEquals(4, (L + "0123").match(charStream, 0))
            assertEquals(10, (L + L_digit).repeat().match(charStream, 0))
            assertEquals(1, (L + L_word).match(charStream, 0))
            assertEquals(10, (L + "01" + L_any.until(L + "89")).match(charStream, 0))
            assertEquals(3, (L - "DF").join(L+"E").match(charStream, 13))
            assertEquals(3, (L - "DF").interlace(L+"E").match(charStream, 13))
            assertEquals(6, (L - "ABCDE").unless(L+"F").match(charStream, 10))
            assertEquals(6, (L - "ABCDE").until(L+"F").match(charStream, 10))
            assertEquals(10, (L_digit.repeat() + (L + "A").test()).match(charStream, 0))
        }
    }

    @Test
    fun matchToken() {
        val charStream = CharReader.fromString(text).toStream(2)
        Grule {
            val t1 by token(L + "01")
            val t2 by token(L + L_digit.repeat(1))
            val t3 by token(L + L_any)
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

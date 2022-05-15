package io.grule.lexer

import io.grule.Grule
import org.junit.Assert.assertEquals
import org.junit.Test

class LexerSimpleTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun matchLexer() {
        val charStream = CharReader.fromString(text).toStream(2)
        Lexer.run {
            assertEquals(4, (X + "0123").match(charStream, 0))
            assertEquals(10, (DIGIT).repeat().match(charStream, 0))
            assertEquals(1, (WORD).match(charStream, 0))
            assertEquals(10, (X + "01" + ANY.untilNonGreedy(ANY + "89")).match(charStream, 0))
            assertEquals(3, (X - "DF").join(X + "E").match(charStream, 13))
            assertEquals(3, (X - "DF").interlace(X + "E").match(charStream, 13))
            assertEquals(6, (X - "ABCDE").untilGreedy(X + "F").match(charStream, 10))
            assertEquals(6, (X - "ABCDE").untilNonGreedy(X + "F").match(charStream, 10))
            assertEquals(10, (DIGIT.repeat() + (X + "A").test()).match(charStream, 0))
        }
    }

    @Test
    fun matchToken() {
        val charStream = CharReader.fromString(text).toStream(2)
        Grule {
            val t1 by token { X + "01" }
            val t2 by token { DIGIT.repeat(1) }
            val t3 by token { ANY }
            println(t1.name)
            println(t2.name)
            println(t3.name)

            val scanner = tokenStream(charStream)
            assertEquals(t1, scanner.peek(0).scanner)
            assertEquals(t2, scanner.peek(1).scanner)
            assertEquals(t3, scanner.peek(2).scanner)
            assertEquals(Scanners.EOF, scanner.peek(15).scanner)
            println(scanner)
        }
    }
}

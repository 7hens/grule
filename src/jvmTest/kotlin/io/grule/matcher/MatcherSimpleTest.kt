package io.grule.matcher

import io.grule.lexer.Lexer
import io.grule.lexer.LexerDsl
import io.grule.lexer.LexerMatcher
import io.grule.lexer.LexerMatcherStatus
import io.grule.token.CharReader
import io.grule.token.CharStream
import org.junit.Assert.assertEquals
import org.junit.Test

class MatcherSimpleTest {
    private val text = "0123456789ABCDEF"

    @Test
    fun matchLexer() {
        val charStream = CharReader.fromString(text).toStream(2)
        fun LexerMatcher.match(charStream: CharStream, offset: Int): Int {
            val status = LexerMatcherStatus.from(charStream).next(offset)
            return status.apply(this).position - offset
        }
        LexerDsl {
            assertEquals(4, (X + "0123").match(charStream, 0))
            assertEquals(10, (DIGIT).repeat().match(charStream, 0))
            assertEquals(1, (WORD).match(charStream, 0))
            assertEquals(10, (X + "01" + ANY.until(ANY + "89")).match(charStream, 0))
            assertEquals(3, (X - "DF").join(X + "E").match(charStream, 13))
            assertEquals(3, (X - "DF").interlace(X + "E").match(charStream, 13))
            assertEquals(6, (X - "ABCDE").till(X + "F").match(charStream, 10))
            assertEquals(6, (X - "ABCDE").until(X + "F").match(charStream, 10))
            assertEquals(10, (DIGIT.repeat() + (X + "A").test()).match(charStream, 0))
        }
    }

    @Test
    fun matchToken() {
        val charStream = CharReader.fromString(text).toStream(2)
        val lexer = Lexer.factory()
        val t1 by lexer { X + "01" }
        val t2 by lexer { DIGIT.more() }
        val t3 by lexer { ANY }
        println(t1)
        println(t2)
        println(t3)

        val scanner = lexer.tokenStream(charStream)
        assertEquals(t1, scanner.peek(0).lexer)
        assertEquals(t2, scanner.peek(1).lexer)
        assertEquals(t3, scanner.peek(2).lexer)
        assertEquals(Lexer.EOF, scanner.peek(15).lexer)
        println(scanner)
    }
}

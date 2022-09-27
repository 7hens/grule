package io.grule.lexer

import io.grule.util.log.Logger
import org.junit.Before
import org.junit.Test

class LexerPerformanceTest {

    @Before
    fun before() {
        Logger.setDefault(Logger.builder().verbose)
        Logger.info("START")
    }

    @Test(timeout = 1000L)
    fun plus() {
        val count = 0 until 10000
        val source = count.joinToString("") { "ABC" }
        val lexer = LexerFactory()
        val abc by lexer { count.fold(X) { acc, _ -> acc + "A" + "BC" } }
        val tokenStream = lexer.tokenStream(source)

        abc.toString()
        Logger.info("AFTER tokenStream")
        tokenStream.all().forEach { Logger.info(it) }
    }

    @Test(timeout = 1000L)
    fun or() {
        val count = 0 until 10000
        val source = count.joinToString("") { "ABC" }
        val lexer = LexerFactory()
        val abc by lexer { X + "ABCABCABC" or X + "ABCABC" or X + "ABC" }
        val tokenStream = lexer.tokenStream(source)

        abc.toString()
        Logger.info("AFTER tokenStream")
        tokenStream.all().forEach { Logger.info(it) }
    }
}
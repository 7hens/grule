package io.grule.matcher2.parser

import io.grule.matcher2.lexer.LexerDsl

/**
 * ParserDslTest.
 * @author huangzhenzi
 */
class ParserDslTest {
    fun test() {
        val num = LexerDsl { digit }
        val op = LexerDsl { "+-*/" }
//        val parser = ParserDsl { X + num }
    }
}
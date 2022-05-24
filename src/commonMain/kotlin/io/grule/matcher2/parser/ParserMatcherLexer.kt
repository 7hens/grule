package io.grule.matcher2.parser

import io.grule.lexer.Lexer
import io.grule.matcher2.MatcherException

internal class ParserMatcherLexer(private val lexer: Lexer) : ParserMatcher {

    override fun match(context: ParserMatcherContext, offset: Int): Int {
        val token = context.peek(offset)
        if (token.lexer == lexer) {
            return 1
        }
        throw MatcherException(context, offset)
    }

    override fun toString(): String {
        return "<$lexer>"
    }
}
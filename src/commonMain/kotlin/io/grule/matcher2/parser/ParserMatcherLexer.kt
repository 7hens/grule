package io.grule.matcher2.parser

import io.grule.lexer.Lexer
import io.grule.matcher2.MatcherException
import io.grule.node.AstNode

internal class ParserMatcherLexer(private val lexer: Lexer) : ParserMatcher {

    override fun match(status: ParserMatcherContext): ParserMatcherContext {
        val token = status.peek()
        if (token.lexer == lexer) {
            return status.next(AstNode.of(lexer, token))
        }
        throw MatcherException(status)
    }

    override fun toString(): String {
        return "<$lexer>"
    }
}
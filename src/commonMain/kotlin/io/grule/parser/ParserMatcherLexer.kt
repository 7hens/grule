package io.grule.parser

import io.grule.lexer.Lexer

internal class ParserMatcherLexer(private val lexer: Lexer) : ParserMatcher {

    override fun match(status: ParserStatus): ParserStatus {
        val token = status.peek()
        if (token.lexer == lexer) {
            return status.next(lexer.newNode(token))
        }
        status.panic(lexer)
    }

    override fun toString(): String {
        return "<$lexer>"
    }
}
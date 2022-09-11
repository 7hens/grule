package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.node.AstNode

internal class ParserMatcherLexer(private val lexer: Lexer) : ParserMatcher {

    override fun match(status: ParserMatcherStatus): ParserMatcherStatus {
        val token = status.peek()
        if (token.lexer == lexer) {
            return status.next(AstNode.of(lexer, token))
        }
        status.panic(lexer)
    }

    override fun toString(): String {
        return "<$lexer>"
    }
}
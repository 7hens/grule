package io.grule.matcher2.parser

import io.grule.matcher2.MatcherException
import io.grule.node.AstNode

internal class ParserMatcherString(val text: String) : ParserMatcher {

    override fun match(status: ParserMatcherContext): ParserMatcherContext {
        val token = status.peek()
        if (token.text == text) {
            return status.next(AstNode.of(text, token))
        }
        throw MatcherException(status)
    }

    override fun toString(): String {
        return text
    }
}
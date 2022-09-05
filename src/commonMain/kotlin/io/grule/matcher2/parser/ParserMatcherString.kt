package io.grule.matcher2.parser

import io.grule.node.AstNode

internal class ParserMatcherString(val text: String) : ParserMatcher {

    override fun match(status: ParserMatcherStatus): ParserMatcherStatus {
        val token = status.peek()
        if (token.text == text) {
            return status.next(AstNode.of(text, token))
        }
        status.panic(text)
    }

    override fun toString(): String {
        return text
    }
}
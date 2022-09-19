package io.grule.parser

import io.grule.node.KeyOwner

internal class ParserMatcherString(val text: String) : ParserMatcher {

    override fun match(status: ParserStatus): ParserStatus {
        val token = status.peek()
        if (token.text == text) {
            return status.next(KeyOwner(text).newNode(token))
        }
        status.panic(text)
    }

    override fun toString(): String {
        return text
    }
}
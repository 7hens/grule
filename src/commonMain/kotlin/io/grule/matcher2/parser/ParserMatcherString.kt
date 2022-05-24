package io.grule.matcher2.parser

import io.grule.matcher2.MatcherException

internal class ParserMatcherString(val text: String) : ParserMatcher {
    
    override fun match(context: ParserMatcherContext, offset: Int): Int {
        val token = context.peek(offset)
        if (token.text == text) {
            return 1
        }
        throw MatcherException(context, offset)
    }

    override fun toString(): String {
        return text
    }
}
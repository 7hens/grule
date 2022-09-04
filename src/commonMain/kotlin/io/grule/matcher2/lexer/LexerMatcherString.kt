package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherException

internal class LexerMatcherString(private val text: String) : LexerMatcher {

    override fun match(status: LexerMatcherContext): LexerMatcherContext {
        status.peek(text.length)
        if (status.peek() == null) {
            throw MatcherException(status)
        }
        val actualText = status.getText(text.length)
        if (actualText == text) {
            return status.next(text.length)
        }
        throw MatcherException(status)
    }

    override fun toString(): String {
        return text
    }
}

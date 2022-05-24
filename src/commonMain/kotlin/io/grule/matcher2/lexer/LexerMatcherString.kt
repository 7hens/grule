package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherException

internal class LexerMatcherString(private val text: String) : LexerMatcher {

    override fun match(context: LexerMatcherContext, offset: Int): Int {
        context.peek(offset + text.length)
        if (context.peek(offset) == null) {
            throw MatcherException(context, offset)
        }
        val actualText = context.getText(offset, offset + text.length)
        if (actualText == text) {
            return text.length
        }
        throw MatcherException(context, offset)
    }

    override fun toString(): String {
        return text
    }
}

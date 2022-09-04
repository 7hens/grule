package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherException

internal class LexerMatcherString(private val text: String) : LexerMatcher {

    override fun match(context: LexerMatcherContext): LexerMatcherContext {
        context.peek(text.length)
        if (context.peek() == null) {
            throw MatcherException(context)
        }
        val actualText = context.getText(text.length)
        if (actualText == text) {
            return context.next(text.length)
        }
        throw MatcherException(context)
    }

    override fun toString(): String {
        return text
    }
}

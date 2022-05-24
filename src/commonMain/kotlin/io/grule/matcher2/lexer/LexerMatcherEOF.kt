package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherException

internal object LexerMatcherEOF : LexerMatcher {
    override fun match(context: LexerMatcherContext, offset: Int): Int {
        context.peek(offset) ?: return 0
        throw MatcherException(context, offset)
    }

    override fun toString(): String {
        return "EOF"
    }
}
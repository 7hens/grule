package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherException

internal object LexerMatcherEOF : LexerMatcher {
    override fun match(status: LexerMatcherContext): LexerMatcherContext {
        status.peek() ?: return status
        throw MatcherException(status)
    }

    override fun toString(): String {
        return "EOF"
    }
}
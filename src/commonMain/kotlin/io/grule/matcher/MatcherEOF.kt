package io.grule.matcher

import io.grule.token.MatcherContext

internal object MatcherEOF : Matcher {
    override fun match(context: MatcherContext, offset: Int): Int {
        val c = context.peek(offset) ?: return 0
        throw MatcherException(context, c.toString(), toString())
    }

    override fun toString(): String {
        return "EOF"
    }
}
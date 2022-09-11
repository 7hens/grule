package io.grule.matcher

import io.grule.token.MatcherContext

class MatcherShadow : Matcher {
    private val error = UnsupportedOperationException("shadow matcher")

    override fun match(context: MatcherContext, offset: Int): Int {
        throw error
    }

    override fun plus(matcher: Matcher): Matcher {
        return MatcherPlus(listOf(matcher))
    }

    override fun or(matcher: Matcher): Matcher {
        return MatcherOr(listOf(matcher))
    }

    override fun toString(): String {
        return "<X>"
    }
}
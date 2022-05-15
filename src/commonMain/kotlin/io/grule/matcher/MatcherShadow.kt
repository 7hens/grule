package io.grule.matcher

object MatcherShadow : Matcher {
    override fun match(context: MatcherContext, offset: Int): Int {
        throw UnsupportedOperationException("shadow lexer")
    }

    override fun plus(matcher: Matcher): Matcher {
        return MatcherPlus(listOf(matcher))
    }

    override fun or(matcher: Matcher): Matcher {
        return MatcherOr(listOf(matcher))
    }
}
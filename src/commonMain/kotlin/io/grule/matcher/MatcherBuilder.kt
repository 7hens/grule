package io.grule.matcher

internal open class MatcherBuilder : Matcher {
    private var myMatcher: Matcher = MatcherShadow

    override fun match(context: MatcherContext, offset: Int): Int {
        return myMatcher.match(context, offset)
    }

    override fun plus(matcher: Matcher): Matcher {
        myMatcher = myMatcher.plus(matcher)
        return this
    }

    override fun or(matcher: Matcher): Matcher {
        myMatcher = myMatcher.or(matcher)
        return this
    }

    override fun toString(): String {
        return myMatcher.toString()
    }

}
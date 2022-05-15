package io.grule.matcher

internal class MatcherTest(private val matcher: Matcher) : Matcher {
    override fun match(context: MatcherContext, offset: Int): Int {
        matcher.match(context, offset)
        return 0
    }

    override fun toString(): String {
        return "(?$matcher)"
    }
}
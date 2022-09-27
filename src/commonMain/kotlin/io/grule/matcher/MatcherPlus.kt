package io.grule.matcher

internal class MatcherPlus<T : Status<T>>(val matchers: List<Matcher<T>>) : Matcher<T> {

    override fun match(status: T): T {
        return matchers.fold(status) { acc, matcher -> matcher.match(acc) }
    }

    override fun matchesEmpty(): Boolean {
        return matchers.all { it.matchesEmpty() }
    }

    override fun plus(matcher: Matcher<T>): Matcher<T> {
        if (matcher is MatcherPlus) {
            return MatcherPlus(matchers + matcher.matchers)
        }
        return MatcherPlus(matchers + matcher)
    }

    override fun toString(): String {
        return matchers.joinToString(" ")
    }
}
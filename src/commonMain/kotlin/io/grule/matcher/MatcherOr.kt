package io.grule.matcher

internal class MatcherOr<T : Status<T>>(val matchers: List<Matcher<T>>) : Matcher<T> {

    override fun match(status: T): T {
        var error: MatcherException? = null
        for (matcher in matchers) {
            try {
                return matcher.match(status)
            } catch (e: MatcherException) {
                error = e
            }
        }
        throw error!!
    }

    override fun or(matcher: Matcher<T>): Matcher<T> {
        if (matcher is MatcherOr) {
            return MatcherOr(matchers + matcher.matchers)
        }
        return MatcherOr(matchers + matcher)
    }

    override fun matchesEmpty(): Boolean {
        return matchers.any { it.matchesEmpty() }
    }

    override fun toString(): String {
        return matchers.joinToString(" | ")
    }
}
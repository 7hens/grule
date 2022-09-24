package io.grule.matcher

internal class MatcherTest<T : Status<T>>(private val matcher: Matcher<T>) : Matcher<T> {

    override fun match(status: T): T {
        matcher.match(status)
        return status
    }

    override fun matchesEmpty(): Boolean {
        return true
    }

    override fun toString(): String {
        return "(? $matcher)"
    }
}

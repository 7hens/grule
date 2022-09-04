package io.grule.matcher2

internal class MatcherTest<T>(private val matcher: Matcher<T>) : Matcher<T> {

    override fun match(status: T): T {
        matcher.match(status)
        return status
    }

    override fun toString(): String {
        return "(? $matcher)"
    }
}
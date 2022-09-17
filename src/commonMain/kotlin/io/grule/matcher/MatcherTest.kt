package io.grule.matcher

internal class MatcherTest<T : Status<T>>(private val matcher: Matcher<T>) : Matcher<T> {

    override fun match(status: T): T {
        status.apply(matcher)
        return status
    }

    override fun toString(): String {
        return "(? $matcher)"
    }
}

package io.grule.matcher

internal class MatcherShadow<T : Matcher.Status<T>> : Matcher<T> {
    override val isEmpty: Boolean = true

    override fun match(status: T): T {
        throw MatcherException(status)
    }

    override fun plus(matcher: Matcher<T>): Matcher<T> {
        return matcher
    }

    override fun or(matcher: Matcher<T>): Matcher<T> {
        return matcher
    }

    override fun times(minTimes: Int, maxTimes: Int): Matcher<T> {
        throw UnsupportedOperationException()
    }

    override fun self(fn: Matcher.Self<T>.() -> Matcher<T>): Matcher<T> {
        throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return "()"
    }
}
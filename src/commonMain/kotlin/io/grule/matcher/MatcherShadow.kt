package io.grule.matcher

internal class MatcherShadow<T : Status<T>> : ReversedMatcher<T> {
    override val reverser: Matcher<T> = this
    override val isEmpty: Boolean = true

    override fun match(status: T): T {
        throw MatcherException(status)
    }

    override fun not(): Matcher<T> {
        return this
    }

    override fun test(): Matcher<T> {
        return this
    }

    override fun plus(matcher: Matcher<T>): Matcher<T> {
        return matcher
    }

    override fun or(matcher: Matcher<T>): Matcher<T> {
        return matcher
    }

    override fun times(minTimes: Int, maxTimes: Int): Matcher<T> {
        return this
    }

    override fun join(separator: Matcher<T>): Matcher<T> {
        return this
    }

    override fun interlace(separator: Matcher<T>): Matcher<T> {
        return separator.optional()
    }

    override fun until(terminal: Matcher<T>): Matcher<T> {
        return terminal
    }

    override fun till(terminal: Matcher<T>): Matcher<T> {
        return terminal
    }

    override fun toString(): String {
        return "()"
    }
}
package io.grule.matcher

internal class MatcherShadow<T : Matcher.Status<T>> : Matcher<T> {

    override fun plus(matcher: Matcher<T>): Matcher<T> {
        return matcher
    }

    override fun or(matcher: Matcher<T>): Matcher<T> {
        return matcher
    }

    override fun match(status: T): T {
        throw MatcherException(status)
    }
}
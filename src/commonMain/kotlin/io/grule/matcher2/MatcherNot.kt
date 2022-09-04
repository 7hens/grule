package io.grule.matcher2

internal class MatcherNot<T>(private val matcher: Matcher<T>) : Matcher<T> {

    override fun match(status: T): T {
        try {
            matcher.match(status)
        } catch (e: MatcherException) {
            return status
        }
        throw MatcherException(status.toString())
    }

    override fun not(): Matcher<T> {
        return matcher
    }

    override fun toString(): String {
        return "(! $matcher)"
    }
}
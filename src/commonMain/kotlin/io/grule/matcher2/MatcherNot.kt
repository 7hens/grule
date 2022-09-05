package io.grule.matcher2

internal class MatcherNot<T : Matcher.Status<T>>(private val matcher: Matcher<T>) : Matcher<T> {

    override fun match(status: T): T {
        try {
            status.apply(matcher)
        } catch (e: MatcherException) {
            return status.next()
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
package io.grule.matcher

internal class MatcherOr<T : Status<T>>(val primary: Matcher<T>, val secondary: Matcher<T>) : Matcher<T> {

    override fun match(status: T): T {
        return try {
            primary.match(status)
        } catch (_: MatcherException) {
            secondary.match(status)
        }
    }

    override fun toString(): String {
        return "$primary | $secondary"
    }
}
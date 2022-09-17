package io.grule.matcher

internal class MatcherOr<T : Status<T>>(val primary: Matcher<T>, val secondary: Matcher<T>) : Matcher<T> {

    override fun match(status: T): T {
        return try {
            status.apply(primary)
        } catch (_: MatcherException) {
            status.apply(secondary)
        }
    }

    override fun toString(): String {
        return "$primary | $secondary"
    }
}
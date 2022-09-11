package io.grule.matcher

internal class MatcherPlus<T : Matcher.Status<T>>(
    val first: Matcher<T>,
    val second: Matcher<T>
) : Matcher<T> {

    override fun match(status: T): T {
        return status.apply(first).apply(second)
    }

    override fun toString(): String {
        return "$first $second"
    }
}
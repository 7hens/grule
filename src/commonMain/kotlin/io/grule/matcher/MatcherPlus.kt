package io.grule.matcher

internal class MatcherPlus<T : Status<T>>(
    val first: Matcher<T>,
    val second: Matcher<T>
) : Matcher<T> {

    override fun match(status: T): T {
        return second.match(first.match(status))
    }

    override fun matchesEmpty(): Boolean {
        return first.matchesEmpty() && second.matchesEmpty()
    }

    override fun toString(): String {
        return "$first $second"
    }
}
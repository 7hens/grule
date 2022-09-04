package io.grule.matcher2

internal class MatcherPlus<T>(
    val first: Matcher<T>,
    val second: Matcher<T>
) : Matcher<T> {

    override fun match(status: T): T {
        return second.match(first.match(status))
    }

    override fun toString(): String {
        return "$first + $second"
    }
}
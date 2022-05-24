package io.grule.matcher2

class MatcherShadow<T> : Matcher<T> {
    private val error = UnsupportedOperationException("shadow matcher")

    override fun match(context: T, offset: Int): Int {
        throw error
    }

    override fun plus(matcher: Matcher<T>): Matcher<T>{
        return MatcherPlus(listOf(matcher))
    }

    override fun or(matcher: Matcher<T>): Matcher<T>{
        return MatcherOr(listOf(matcher))
    }

    override fun toString(): String {
        return "<X>"
    }
}
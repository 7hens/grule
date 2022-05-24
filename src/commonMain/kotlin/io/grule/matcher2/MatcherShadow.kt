package io.grule.matcher2

class MatcherShadow<T> : Matcher<T> {
    private val error = UnsupportedOperationException("shadow matcher")

    override fun match(context: T, offset: Int): Int {
        throw error
    }

    override fun toString(): String {
        return "<->"
    }
}
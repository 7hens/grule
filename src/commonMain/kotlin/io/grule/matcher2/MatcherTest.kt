package io.grule.matcher2

internal class MatcherTest<T>(private val matcher: Matcher<T>) : Matcher<T>{
    override fun match(context: T, offset: Int): Int {
        matcher.match(context, offset)
        return 0
    }

    override fun toString(): String {
        return "(?$matcher)"
    }
}

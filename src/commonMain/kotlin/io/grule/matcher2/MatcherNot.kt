package io.grule.matcher2

internal class MatcherNot<T>(private val matcher: Matcher<T>) : Matcher<T>{

    override fun match(context: T, offset: Int): Int {
        try {
            matcher.match(context, offset)
        } catch (e: MatcherException) {
            return 1
        }
        throw MatcherException(context, offset)
    }

    override fun not(): Matcher<T>{
        return matcher
    }

    override fun toString(): String {
        return "(! $matcher)"
    }
}
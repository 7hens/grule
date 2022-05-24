package io.grule.matcher2

open class MatcherBuilder<T, R : MatcherBuilder<T, R>> : Matcher<T> {
    private var delegate: Matcher<T> = MatcherShadow()
    private var matchers = listOf<Matcher<T>>()

    override fun match(context: T, offset: Int): Int {
        return delegate.match(context, offset)
    }

    @Suppress("UNCHECKED_CAST")
    private fun set(matcher: Matcher<T>): R {
        if (matcher != this) {
            delegate = matcher
        }
        return this as R
    }

    operator fun plus(matcher: Matcher<T>): R {
        matchers = getMatchers(delegate is MatcherPlus) + matcher
        return set(MatcherPlus(matchers))
    }


    infix fun or(matcher: Matcher<T>): Matcher<T> {
        matchers = getMatchers(delegate is MatcherPlus) + matcher
        return set(MatcherOr(matchers))
    }

    private fun getMatchers(condition: Boolean): List<Matcher<T>> {
        return if (condition) matchers else listOf(delegate)
    }

    override fun toString(): String {
        return delegate.toString()
    }
}
package io.grule.matcher2

open class MatcherBuilder<T, R : MatcherBuilder<T, R>> : Matcher<T> {
    private var delegate: Matcher<T> = MatcherShadow()

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
        val matchers = delegate.let { if (it is MatcherPlus) it.matchers else listOf(matcher) }
        return set(MatcherPlus(matchers + matcher))
    }


    infix fun or(matcher: Matcher<T>): Matcher<T> {
        val matchers = delegate.let { if (it is MatcherOr) it.matchers else listOf(matcher) }
        return set(MatcherOr(matchers + matcher))
    }

    override fun toString(): String {
        return delegate.toString()
    }
}